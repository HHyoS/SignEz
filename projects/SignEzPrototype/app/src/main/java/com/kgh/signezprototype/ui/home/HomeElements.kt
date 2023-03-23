package com.kgh.signezprototype.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kgh.signezprototype.R
import com.kgh.signezprototype.data.entities.Cabinet
import com.kgh.signezprototype.data.entities.Signage
import com.kgh.signezprototype.ui.components.WhiteButton
import com.kgh.signezprototype.ui.components.FocusBlock

@Composable //지난 분석 결과 틀
fun PastResult(
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = MaterialTheme.shapes.extraSmall,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline

        ),
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .clickable(onClick = {})
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "지난 분석 결과",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 3.dp)
                        .align(alignment = Alignment.Top),
                    style = MaterialTheme.typography.bodyLarge
                )
                Box {
                    Canvas(modifier = Modifier.size(56.dp)) {
                        val strokeWidth = 4.dp.toPx()
                        val sizePx = 30.dp.toPx()
                        val path = Path().apply {
                            moveTo(strokeWidth * 2, strokeWidth * 2)
                            lineTo(sizePx - strokeWidth * 2, sizePx / 2)
                            lineTo(strokeWidth * 2, sizePx - strokeWidth * 2)
                        }

                        drawPath(
                            path = path,
                            color = Color.Black,
                            style = Stroke(width = strokeWidth)
                        )
                    }
                }
            } // Row 끝
            Text(text = "최근 기록 이미지 슬라이드", modifier = Modifier.padding(10.dp))
        } // Col 끝
    }
}

@Composable // 사이니지 스펙 틀
fun SignEzSpec(
    modifier: Modifier = Modifier,
    navigateToSignageList: () -> Unit,
    signage: Signage?
) {
//    Surface(
//        shape = MaterialTheme.shapes.large,
//        border = BorderStroke(
//            width = 1.dp,
//            color = MaterialTheme.colorScheme.outline
//
//        ),
//        modifier = modifier
//            .clip(MaterialTheme.shapes.small)
//            .clickable(onClick = navigateToSignageList)
//    ) {
//        Column {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth(0.9f),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text("사이니지 스펙", Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
//                OutlinedButton(
//                    onClick = {},
//                    shape = RoundedCornerShape(20.dp),
//                    border = BorderStroke(2.dp, Color.Blue),
//                    colors = ButtonDefaults.outlinedButtonColors(
//                        contentColor = Color.Blue
//                    ),
//                    modifier = Modifier
//                        .padding(4.dp)
//                ) {
//                    Text("입력")
//                }
//            }
//            Text(text = "정보 입력이 필요 합니다.", modifier = Modifier.padding(10.dp))
//        }

    //ㅇㅇㅇㅇㅇㅇ
//    FocusBlock(
//        title = stringResource(id = R.string.signage_spec_title), subtitle = stringResource(id = R.string.need_signage_info), modifier = modifier
//            .clip(MaterialTheme.shapes.small)
//            .clickable(onClick = navigateToSignageList)
//    )
    FocusBlock(
        title = stringResource(id = R.string.signage_spec_title),
        subtitle = stringResource(id = R.string.need_signage_info),
        buttonTitle = "입력",
        isbuttonVisible = true,
        buttonOnclickEvent = navigateToSignageList,
        modifier = Modifier,
    )
    if (signage != null) {
        Text(text = "설치 장소 = ${signage.name}")
        Text(text = "높이 : ${signage.width}")
        Text(text = "너비 : ${signage.height}")
    }

//}
}

@Composable // 캐비닛 스펙 틀
fun CabinetSpec(
    modifier: Modifier = Modifier,
    cabinet: Cabinet?
) {
//    Surface(
//        shape = MaterialTheme.shapes.large,
//        border = BorderStroke(
//            width = 1.dp,
//            color = MaterialTheme.colorScheme.outline
//
//        ),
//        modifier = modifier
//            .clip(MaterialTheme.shapes.small)
//            .clickable(onClick = {})
//    ) {
//        Column {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth(0.9f),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text("캐비닛 스펙", Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
//            }
//            Text(text = "사이니지의 캐비닛 정보", modifier = Modifier.padding(10.dp))
//        }
//    }

    ///ㅇㅇㅇㅇㅇㅇㅇㅇ
//    FocusBlock(
//        title = stringResource(id = R.string.cabinet_spec_title), subtitle = stringResource(id = R.string.need_cabinet_info), modifier = modifier
//    )
    FocusBlock(
        title = stringResource(id = R.string.cabinet_spec_title),
        subtitle = stringResource(id = R.string.need_cabinet_info),
        buttonTitle = null,
        isbuttonVisible = false,
        buttonOnclickEvent = {},
        modifier = Modifier
    )
    if (cabinet != null) {
        Text(text = "설치 장소 = ${cabinet.name}")
        Text(text = "높이 : ${cabinet.cabinetHeight}")
        Text(text = "너비 : ${cabinet.cabinetWidth}")
        Text(text = "모듈 : ${cabinet.moduleColCount}X${cabinet.moduleRowCount}")
    }
}

@Composable
fun PictureAnalysisBtn(navigateToPicture: () -> Unit) {
    WhiteButton(
        title = stringResource(id = R.string.analyze_photo),
        isUsable = true,
        onClickEvent = navigateToPicture
    )

}

@Composable
fun VideoAnalysisBtn(navigateToVideo: () -> Unit) {
    WhiteButton(
        title = stringResource(id = R.string.analyze_video),
        isUsable = true,
        onClickEvent = navigateToVideo
    )

}