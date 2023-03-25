package com.kgh.signezprototype.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kgh.signezprototype.R
import com.kgh.signezprototype.data.entities.Cabinet
import com.kgh.signezprototype.data.entities.Signage
import com.kgh.signezprototype.ui.components.WhiteButton
import com.kgh.signezprototype.ui.components.FocusBlock
import com.kgh.signezprototype.ui.theme.SignEzPrototypeTheme

@Composable //지난 분석 결과 틀
fun PastResult(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .background(androidx.compose.material.MaterialTheme.colors.surface)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "지난 분석 결과",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f),
//                        .align(alignment = Alignment.Top),
                    style = MaterialTheme.typography.bodyLarge
                )
                androidx.compose.material.IconButton(onClick = {}) {
                    androidx.compose.material.Icon(
                        painter = painterResource(id = R.drawable.arrow_forward_ios_fill1_wght300_grad0_opsz48),
                        contentDescription = "지난 결과 보기",
                        tint = androidx.compose.material.MaterialTheme.colors.onSurface,
                        modifier = Modifier
                            .height(21.dp)
                            .width(21.dp)
                            .padding(end = 5.dp)
                    )
                }
            } // Row 끝
            Spacer(modifier = Modifier.padding(50.dp))
        } // Col 끝
    }
}

@Preview
@Composable
fun ComponentPreview() {
    SignEzPrototypeTheme(darkTheme = false) {
        Column {
            PastResult()
        }
    }
}

@Composable // 사이니지 스펙 틀
fun SignEzSpec(
    modifier: Modifier = Modifier,
    navigateToSignageList: () -> Unit,
    signage: Signage?
) {
    if (signage != null) {
        FocusBlock(
            title = stringResource(id = R.string.signage_spec_title),
            subtitle = "${signage.name}",
            infols = listOf("너비 : ${signage.width}", "높이 : ${signage.height}"),
            buttonTitle = "입력",
            isbuttonVisible = true,
            buttonOnclickEvent = navigateToSignageList,
            modifier = Modifier,
        )
    } else if (signage == null) {
        FocusBlock(
            title = stringResource(id = R.string.signage_spec_title),
            infols = listOf(stringResource(id = R.string.need_signage_info)),
            buttonTitle = "입력",
            isbuttonVisible = true,
            buttonOnclickEvent = navigateToSignageList,
            modifier = Modifier,
        )
    }

//    if (signage != null) {
//        Text(text = "설치 장소 = ${signage.name}")
//        Text(text = "높이 : ${signage.width}")
//        Text(text = "너비 : ${signage.height}")
//    }


//}
}

@Composable // 캐비닛 스펙 틀
fun CabinetSpec(
    modifier: Modifier = Modifier,
    cabinet: Cabinet?
) {
    if (cabinet != null) {
        FocusBlock(
            title = stringResource(id = R.string.cabinet_spec_title),
            subtitle = "${cabinet.name}",
            infols = listOf(
                "너비 : ${cabinet.cabinetWidth}",
                "높이 : ${cabinet.cabinetHeight}",
                "모듈 : ${cabinet.moduleColCount}X${cabinet.moduleRowCount}"
            ),
            buttonTitle = null,
            isbuttonVisible = false,
            buttonOnclickEvent = {},
            modifier = Modifier
        )

    } else if (cabinet == null) {
        FocusBlock(
            title = stringResource(id = R.string.cabinet_spec_title),
            infols = listOf(stringResource(id = R.string.need_cabinet_info)),
            buttonTitle = null,
            isbuttonVisible = false,
            buttonOnclickEvent = {},
            modifier = Modifier
        )
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