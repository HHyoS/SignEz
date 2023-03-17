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
    meanb = np.zeros((w*2, h*2))
    meang = np.zeros((w*2, h*2))
    meanr = np.zeros((w*2, h*2))

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
    comb = np.zeros((w*2, h*2))
    comg = np.zeros((w*2, h*2))
    comr = np.zeros((w*2, h*2))

    # BGR 정보 저장
    for i in range(w*2):
        for j in range(h*2):
            j1 = int(j*module_height/2)
            j2 = int((j+1)*module_height/2)
            i1 = int(i*module_width/2)
            i2 = int((i+1)*module_width/2)
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
                        and outer_module(comb, comg, comr, i, j, threshold2)
                        and inner_module(comb, comg, comr, i, j, threshold3)
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


def is_module_changed(meanb, meang, meanr, comb, comg, comr, i, j, threshold):
    for ii in range(2*i, 2*i+2):
        for jj in range(2*j, 2*j+2):
            b = abs(meanb[ii][jj] - comb[ii][jj])
            g = abs(meang[ii][jj] - comg[ii][jj])
            r = abs(meanr[ii][jj] - comr[ii][jj])
            # if (i is 25 and j is 37):
            #     print("B: {}, G: {}, R: {}".format(b, g, r))
            if (b > threshold or g > threshold or r > threshold):
                return True
    return False


def outer_module(comb, comg, comr, i, j, threshold):
    order = [
        [2*i, 2*j, 2*i-1, 2*j], [2*i, 2*j, 2*i, 2*j-1],
        [2*i+1, 2*j, 2*i+2, 2*j], [2*i+1, 2*j, 2*i, 2*j-1],
        [2*i, 2*j+1, 2*i-1, 2*j], [2*i, 2*j+1, 2*i, 2*j+2],
        [2*i+1, 2*j+1, 2*i+2, 2*j], [2*i+1, 2*j+1, 2*i, 2*j+2]
    ]
    for o in order:
        bb = abs(comb[o[0]][o[1]]-comb[o[2]][o[3]])
        gg = abs(comg[o[0]][o[1]]-comg[o[2]][o[3]])
        rr = abs(comr[o[0]][o[1]]-comr[o[2]][o[3]])
        # if (i == 25 and j == 37):
        #     print("B: {}, G: {}, R: {}".format(bb, gg, rr))
        if (bb < threshold and gg < threshold and rr < threshold):
            return False
    return True


def inner_module(comb, comg, comr, i, j, threshold):
    b = np.zeros((2, 2))
    g = np.zeros((2, 2))
    r = np.zeros((2, 2))
    for ii in range(2):
        for jj in range(2):
            b[ii][jj] = comb[2*i+ii][2*j+jj]
            g[ii][jj] = comg[2*i+ii][2*j+jj]
            r[ii][jj] = comr[2*i+ii][2*j+jj]
    order = [[0, 0, 0, 1], [0, 0, 1, 0], [0, 0, 1, 1],
             [0, 1, 1, 1], [0, 1, 1, 0], [1, 0, 1, 1]]
    for o in order:
        bb = abs(b[o[0]][o[1]]-b[o[2]][o[3]])
        gg = abs(g[o[0]][o[1]]-g[o[2]][o[3]])
        rr = abs(r[o[0]][o[1]]-r[o[2]][o[3]])
        if (bb > threshold or gg > threshold or rr > threshold):
            return False
    return True


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

    threshold1 = 30
    threshold2 = 5
    threshold3 = 20

    print(cv2.__version__)
    detect(vid, cabinet_row_cnt, cabinet_column_cnt, cabinet_width,
           cabinet_height, module_row_count, module_column_count, threshold1, threshold2, threshold3)
