import cv2
import numpy as np


def detect(vid, cabinet_row_cnt, cabinet_column_cnt, cabinet_width, cabinet_height, module_row_count, module_column_count, threshold1=10, threshold2=30, threshold3=50):
    # 비디오 파일 열기
    cap = cv2.VideoCapture(vid)

    # 변수 지정
    width = cabinet_row_cnt * cabinet_width
    height = cabinet_column_cnt * cabinet_height
    module_width = int(cabinet_width/module_row_count)
    module_height = int(cabinet_height/module_column_count)

    h = int(height/module_height)
    w = int(width/module_width)

    frame_cnt = int(cap.get(cv2.CAP_PROP_FRAME_COUNT))
    print("Frame Count : "+str(frame_cnt))

    # 이전 페이지의 모듈 정보 저장
    meanb = np.zeros((w, h))
    meang = np.zeros((w, h))
    meanr = np.zeros((w, h))

    for i in range(1, frame_cnt):
        # for i in range(276, 277):
        # for i in range(48, 51):
        # for i in range(144, 147):
        # for i in range(42, 45):
        # for i in range(35, 50):
        # for i in range(273, 277):
        print(i)

        # 프레임을 읽어서 표시
        cap.set(cv2.CAP_PROP_POS_FRAMES, i)
        ret, frame = cap.read()

        if ret:
            try:
                result = roi(frame, width, height, i)
                # 내부 영역만 추출된 이미지를 저장합니다.
                result, is_error_module, meanb, meang, meanr = detect_error(
                    result, width, height, module_width, module_height, meanb, meang, meanr, threshold1, threshold2, threshold3)
                if (is_error_module):
                    cv2.imwrite("frame" + str(i) + ".jpg", result)

            except:
                print('Unable to read frame at ' + str(i) + " frame")
        else:
            print('Unable to read frame at ' + str(i) + " frame")

    # 종료
    cap.release()
    cv2.destroyAllWindows()


def roi(frame, width, height, i):
    # 이미지를 그레이스케일로 변환합니다.
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    gauss = cv2.GaussianBlur(gray, (0, 0), 2)

    # 이미지의 경계선을 찾습니다.
    edged = cv2.Canny(gauss, 30, 80)
    kernel = cv2.getStructuringElement(cv2.MORPH_RECT, (3, 3))
    dilation = cv2.dilate(src=edged, kernel=kernel, iterations=1)
    # eroded = cv2.erode(src=dilation, kernel=kernel, iterations=2)
    # cv2.imwrite("frame" + str(i) + ".jpg", eroded)

    # 경계선에서 contour를 추출합니다.
    contours, hierarchy = cv2.findContours(
        dilation, cv2.RETR_CCOMP, cv2.CHAIN_APPROX_SIMPLE)

    # contour 중 특정 크기 이상의 사각형인 contour를 찾습니다.
    # print(len(contours))
    contours = list(
        filter(lambda c: cv2.contourArea(c) > 100000, contours))
    contours = list(map(lambda c: cv2.approxPolyDP(
        c, 0.01*cv2.arcLength(c, True), True), contours))
    # print(len(contours))
    contours = list(filter(lambda x: len(x) == 4, contours))
    # print(len(contours))

    # 면적이 가장 큰 contour를 찾습니다.
    max_contour = max(contours, key=cv2.contourArea)

    # 꼭지점들의 좌표를 정렬합니다.
    approx = np.squeeze(max_contour)
    sums = np.sum(approx, axis=1)
    diffs = np.diff(approx, axis=1)
    sorted_approx = np.zeros_like(approx)
    sorted_approx[0] = approx[np.argmin(sums)]
    sorted_approx[2] = approx[np.argmax(sums)]
    sorted_approx[1] = approx[np.argmin(diffs)]
    sorted_approx[3] = approx[np.argmax(diffs)]

    # 꼭지점들의 좌표를 구합니다.
    points = np.array(sorted_approx, dtype=np.float32)

    # 변환 전과 후의 좌표를 지정합니다.
    src = np.float32(points)
    dst = np.float32(
        [[0, 0], [width, 0], [width, height], [0, height]])

    # 변환 행렬을 구합니다.
    M = cv2.getPerspectiveTransform(src, dst)

    # 원근 변환을 적용합니다.
    result = cv2.warpPerspective(frame, M, (width, height))
    # cv2.imwrite("frame" + str(i) + ".jpg", result)
    return result


def detect_error(mat, width, height, module_width, module_height, meanb, meang, meanr, threshold1, threshold2, threshold3):
    result = mat
    is_error_module = False
    qw = int(module_width/4)
    qh = int(module_height/4)

    sharpening_mask1 = np.array([[-1, -1, -1], [-1, 9, -1], [-1, -1, -1]])
    sharpening_mask2 = np.array([[0, -1, 0], [-1, 5, -1], [0, -1, 0]])
    embossint_mask2 = np.array([[1, 1, 1], [1, -8, 1], [1, 1, 1]])
    smoothing_mask = np.array(
        [[1/16, 1/8, 1/16], [1/8, 1/4, 1/8], [1/16, 1/8, 1/16]])

    blue = (255, 0, 0)
    green = (0, 255, 0)
    red = (0, 0, 255)
    gray = (127, 127, 127)
    black = (0, 0, 0)

    h = int(height/module_height)
    w = int(width/module_width)

    # result = cv2.filter2D(mat, -1, sharpening_mask1)
    # result = cv2.filter2D(mat, -1, embossint_mask2)

    # BGR 저장용
    comb = np.zeros((w, h))
    comg = np.zeros((w, h))
    comr = np.zeros((w, h))

    # BGR 정보 저장
    for i in range(w):
        for j in range(h):
            j1 = int(j*module_height)
            j2 = int((j+1)*module_height)
            i1 = int(i*module_width)
            i2 = int((i+1)*module_width)
            comb[i][j] = int(cv2.mean(
                mat[j1:j2, i1:i2])[0])
            comg[i][j] = int(cv2.mean(
                mat[j1:j2, i1:i2])[1])
            comr[i][j] = int(cv2.mean(
                mat[j1:j2, i1:i2])[2])

    # ----------------------------- draw lines -----------------------------------------
    # for j in range(1, w):
    #     cv2.line(result, (j*module_height, 0), (j*module_height, height), gray)
    # for j in range(1, h):
    #     cv2.line(result, (0, j*module_width), (width, j*module_width), gray)
    # for j in range(1, w):
    #     cv2.line(result, (j*cabinet_height, 0),
    #              (j*cabinet_height, height), red)
    # for j in range(1, h):
    #     cv2.line(result, (0, j*cabinet_width), (width, j*cabinet_width), red)

    for i in range(w):
        for j in range(h):
            if (j != 0 and j != h-1 and i != 0 and i != w-1):
                if (is_module_changed(meanb, meang, meanr, comb, comg, comr, i, j, threshold1)
                        and outer_module(comb, comg, comr, i, j, threshold2, threshold3)
                    ):
                    is_error_module = True
                    print("Error Detected!!! X: {}, Y: {}".format(i, j))

                    cv2.line(result, (i*module_width, j*module_height),
                             ((i)*module_width, (j+1)*module_height-1), red, 3)
                    cv2.line(result, (i*module_width, j*module_height),
                             ((i+1)*module_width-1, (j)*module_height), red, 3)
                    cv2.line(result, ((i+1)*module_width-1, j*module_height),
                             ((i+1)*module_width-1, (j+1)*module_height-1), red, 3)
                    cv2.line(result, (i*module_width, (j+1)*module_height-1),
                             ((i+1)*module_width-1, (j+1)*module_height-1), red, 3)

                    # cv2.line(result, (i*module_width, j*module_height),
                    #          ((i)*module_width, (j+1)*module_height-1), green)
                    # cv2.line(result, (i*module_width, j*module_height),
                    #          ((i+1)*module_width-1, (j)*module_height), green)
                    # cv2.line(result, ((i+1)*module_width-1, j*module_height),
                    #          ((i+1)*module_width-1, (j+1)*module_height-1), green)
                    # cv2.line(result, (i*module_width, (j+1)*module_height-1),
                    #          ((i+1)*module_width-1, (j+1)*module_height-1), green)

                    # cv2.line(result, (i*module_size, j*module_size),
                    #          ((i+1)*module_size-1, (j+1)*module_size-1), red)
                    # cv2.line(result, ((i+1)*module_size-1, j*module_size),
                    #          (i*module_size, (j+1)*module_size-1), red)

    # if (j is 37 and i > 15 and i < 32):
    #     print(str(com[i][j-1])+" " +
    #           str(com[i][j])+" "+str(com[i][j+1]))
    #     cv2.imwrite("i="+str(i)+", j="+str(j)+".jpg",
    #                 mat[j*module_size:(j+1)*module_size, i*module_size:(i+1)*module_size])

    # _, result = cv2.threshold(
    #     result, 220, 255, cv2.THRESH_BINARY)
    # result = cv2.Canny(result, 30, 50)
    # contours, hierarchy = cv2.findContours(
    #     result, cv2.RETR_CCOMP, cv2.CHAIN_APPROX_SIMPLE)
    # print(len(contours))
    # for con in contours:
    # epsilon = 0.02 * cv2.arcLength(con, True)
    # approx = cv2.approxPolyDP(con, epsilon, True)
    # if (len(approx) is 4):
    # cv2.drawContours(mat, approx, -1, red, 1)

    return result, is_error_module, comb, comg, comr


def is_module_changed(meanb, meang, meanr, comb, comg, comr, i, j, threshold1):
    b = abs(meanb[i][j] - comb[i][j])
    g = abs(meang[i][j] - comg[i][j])
    r = abs(meanr[i][j] - comr[i][j])
    # if (i is 25 and j is 37):
    #     print("B: {}, G: {}, R: {}".format(b, g, r))
    if (b > threshold1 or g > threshold1 or r > threshold1):
        return True
    return False


def outer_module(comb, comg, comr, i, j, threshold2, threshold3):
    accuracy = 0
    order = [
        [i-1, j-1, i+1, j+1], [i-1, j, i+1, j],
        [i, j-1, i, j+1], [i-1, j+1, i+1, j-1]

    ]
    for o in order:
        mb = abs(comb[o[0]][o[1]] - comb[o[2]][o[3]])
        b1 = abs(comb[o[0]][o[1]] - comb[i][j])
        b2 = abs(comb[o[2]][o[3]] - comb[i][j])

        mg = abs(comg[o[0]][o[1]] - comg[o[2]][o[3]])
        g1 = abs(comg[o[0]][o[1]] - comg[i][j])
        g2 = abs(comg[o[2]][o[3]] - comg[i][j])

        mr = abs(comr[o[0]][o[1]] - comr[o[2]][o[3]])
        r1 = abs(comr[o[0]][o[1]] - comr[i][j])
        r2 = abs(comr[o[2]][o[3]] - comr[i][j])

        if (
            (abs(mb - b1 - b2) > threshold2)
            or (abs(mg - g1 - g2) > threshold2)
            or (abs(mr - r1 - r2) > threshold2)
        ):
            accuracy += 1
    return accuracy >= threshold3


if __name__ == "__main__":
    # vid = '20210415_193123_1.mp4'
    # cabinet_row_cnt = 11
    # cabinet_column_cnt = 19

    # vid = '20221017_신세계백화점 깜박임1.mp4'
    vid = '20221017_신세계백화졈 깜박임2.mp4'
    cabinet_row_cnt = 12
    cabinet_column_cnt = 17

    # vid = 'Foothill Video.mov'
    # width_cnt = 12
    # height_cnt = 6

    cabinet_width = 96
    cabinet_height = 96
    module_row_count = 4
    module_column_count = 4

    threshold1 = 50
    threshold2 = 20
    threshold3 = 3

    print(cv2.__version__)
    detect(vid, cabinet_row_cnt, cabinet_column_cnt, cabinet_width,
           cabinet_height, module_row_count, module_column_count, threshold1, threshold2, threshold3)
