import cv2
import numpy as np


def brute(vid, width_cnt, height_cnt, cabinet_size, module_size):
    # 비디오 파일 열기

    # vid = '20221017_신세계백화졈 깜박임2.mp4'
    cap = cv2.VideoCapture(vid)
    # idx = 49
    # max_size = 0

    width = width_cnt * cabinet_size
    height = height_cnt * cabinet_size
    frame_cnt = int(cap.get(cv2.CAP_PROP_FRAME_COUNT))
    print(frame_cnt)

    # for i in range(1, frame_cnt):
    for i in range(48, 51):
        # for i in range(42, 45):
        print(i)

        # 프레임을 읽어서 표시
        cap.set(cv2.CAP_PROP_POS_FRAMES, i)
        ret, frame = cap.read()

        if ret:
            try:
                # 이미지를 그레이스케일로 변환합니다.
                gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
                # gray = cv2.GaussianBlur(gray, (0, 0), 2)
                # gray = cv2.adaptiveThreshold(
                #     gray, 255, cv2.ADAPTIVE_THRESH_MEAN_C, cv2.THRESH_BINARY, 15, 2)

                # 이미지의 경계선을 찾습니다.
                edged = cv2.Canny(gray, 30, 200)

                # 경계선에서 contour를 추출합니다.
                contours, hierarchy = cv2.findContours(
                    edged, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

                # 면적이 가장 큰 contour를 찾습니다.
                max_contour = max(contours, key=cv2.contourArea)
                cv2.imwrite("frame" + str(i) + ".jpg", edged)

                if (cv2.contourArea(max_contour) > 100000):
                    # contour의 꼭지점들을 구합니다.
                    epsilon = 0.02 * cv2.arcLength(max_contour, True)
                    approx = cv2.approxPolyDP(max_contour, epsilon, True)
                    # print(approx)

                    # 꼭지점들의 좌표를 시계 방향으로 정렬합니다.
                    approx = np.squeeze(approx)
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

                    # 내부 영역만 추출된 이미지를 저장합니다.
                    result = smoothing_and_lining(
                        result, width, height, cabinet_size, module_size)
                    cv2.imwrite("frame" + str(i) + ".jpg", result)
                else:
                    gray = cv2.adaptiveThreshold(
                        gray, 255, cv2.ADAPTIVE_THRESH_MEAN_C, cv2.THRESH_BINARY, 15, 2)
                    # 이미지의 경계선을 찾습니다.
                    edged = cv2.Canny(gray, 30, 200)
                    # 경계선에서 contour를 추출합니다.
                    contours, hierarchy = cv2.findContours(
                        edged, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
                    # 면적이 가장 큰 contour를 찾습니다.
                    max_contour = max(contours, key=cv2.contourArea)
                    if (cv2.contourArea(max_contour) > 100000):
                        # contour의 꼭지점들을 구합니다.
                        epsilon = 0.02 * cv2.arcLength(max_contour, True)
                        approx = cv2.approxPolyDP(max_contour, epsilon, True)
                        # print(approx)

                        # 꼭지점들의 좌표를 시계 방향으로 정렬합니다.
                        approx = np.squeeze(approx)
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
                        # result = cv2.bilateralFilter(result, 9, 100, 100)
                        # result = cv2.GaussianBlur(result, (0, 0), 2)
                        # result = cv2.filter2D(result, -1, smoothing_mask)

                        # 내부 영역만 추출된 이미지를 저장합니다.
                        result = smoothing_and_lining(
                            result, width, height, cabinet_size, module_size)
                        cv2.imwrite("frame" + str(i) + ".jpg", result)
            except:
                print("sry")
        else:
            print('Unable to read frame at ' + str(i) + " frame")

    # 종료
    cap.release()
    cv2.destroyAllWindows()


def smoothing_and_lining(mat, width, height, cabinet_size, module_size):
    # sharpening_mask1 = np.array([[-1, -1, -1], [-1, 9, -1], [-1, -1, -1]])
    # sharpening_mask2 = np.array([[0, -1, 0], [-1, 5, -1], [0, -1, 0]])

    smoothing_mask = np.array(
        [[1/16, 1/8, 1/16], [1/8, 1/4, 1/8], [1/16, 1/8, 1/16]])

    green = (0, 255, 0)
    red = (0, 0, 255)
    midgray = (127, 127, 127)
    black = (0, 0, 0)

    result = cv2.filter2D(mat, -1, smoothing_mask)
    for j in range(1, int(width/module_size)):
        cv2.line(result, (j*module_size, 0), (j*module_size, height), midgray)
    for j in range(1, int(height/module_size)):
        cv2.line(result, (0, j*module_size), (width, j*module_size), midgray)
    for j in range(1, int(width/cabinet_size)):
        cv2.line(result, (j*cabinet_size, 0), (j*cabinet_size, height), red)
    for j in range(1, int(height/cabinet_size)):
        cv2.line(result, (0, j*cabinet_size), (width, j*cabinet_size), red)
    return result


if __name__ == "__main__":

    # vid = '20210415_193123_1.mp4'
    # width_cnt = 11
    # height_cnt = 19

    # vid = '20221017_신세계백화점 깜박임1.mp4'
    # vid = '20221017_신세계백화졈 깜박임2.mp4'
    # width_cnt = 12
    # height_cnt = 17

    vid = 'Foothill Video.mov'
    width_cnt = 12
    height_cnt = 6

    cabinet_size = 100
    module_size = 25
    print(cv2.__version__)
    brute(vid, width_cnt, height_cnt, cabinet_size, module_size)
