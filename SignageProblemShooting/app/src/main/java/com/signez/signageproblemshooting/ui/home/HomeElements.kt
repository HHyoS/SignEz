package com.signez.signageproblemshooting.ui.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.signez.signageproblemshooting.R
import com.signez.signageproblemshooting.data.entities.AnalysisResult
import com.signez.signageproblemshooting.data.entities.Cabinet
import com.signez.signageproblemshooting.data.entities.Signage
import com.signez.signageproblemshooting.ui.analysis.AnalysisViewModel
import com.signez.signageproblemshooting.ui.analysis.ResultGridDestination
import com.signez.signageproblemshooting.ui.analysis.ResultsHistoryDestination
import com.signez.signageproblemshooting.ui.components.FocusBlock
import com.signez.signageproblemshooting.ui.components.WhiteButton
import com.signez.signageproblemshooting.ui.inputs.MainViewModel

@Composable //지난 분석 결과 틀
fun PastResult(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: AnalysisViewModel
) {
    val resultListState by viewModel.resultListState.collectAsState()
    val itemList = resultListState.itemList.sortedByDescending { it.resultDate }

    Surface(
        modifier = modifier
            .background(androidx.compose.material.MaterialTheme.colors.surface)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp, top = 10.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "지난 분석 결과",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f),
                    style = MaterialTheme.typography.bodyLarge
                )
                androidx.compose.material.IconButton(
                    onClick = {navController.navigate(ResultsHistoryDestination.route)}
                ) {
                    androidx.compose.material.Icon(
                        painter = painterResource(id = R.drawable.arrow_forward_ios_fill1_wght300_grad0_opsz48),
                        contentDescription = "지난 결과 보기",
                        tint = androidx.compose.material.MaterialTheme.colors.onSurface,
                        modifier = Modifier
                            .height(23.dp)
                            .width(23.dp)
                            .padding(end = 5.dp)
                    )
                }
            } // Row 끝

            LazyRow(
                contentPadding = PaddingValues(
                    start = 24.dp,
                    end = 24.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items = itemList) { item ->
                    CompositionLocalProvider(LocalRippleTheme provides RippleCustomTheme) {
                        HomeHistoryElement(
                            result=item,
                            viewModel=viewModel,
                            navController=navController
                            )
                    }
                }
            }
            Spacer(modifier = Modifier.padding(10.dp))
        } // Col 끝
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun HomeHistoryElement(
    result: AnalysisResult,
    viewModel: AnalysisViewModel,
    navController: NavController
) {
    val signageState = produceState(initialValue = null as Signage?, producer = {
        value = viewModel.getSignageById(result.signageId)
    })
    val signage = signageState.value
    if (signage != null) {
        signage.repImg?.let { byteArray ->
            GlideImage(
                model = byteArray,
                contentDescription = "글라이드",
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
                        viewModel.selectedResultId.value = result.id
                        Log.d("HomeResult", "/${result.id}")
                        navController.navigate(ResultGridDestination.route+"/${result.id}")
                    }
                    .background(color = Color.Black)
            )
        }
    } // if 끝
}

@Composable // 사이니지 스펙 틀
fun SignEzSpec(
    navigateToSignageList: () -> Unit,
    signage: Signage?
) {
    if (signage != null) {
        FocusBlock(
            title = stringResource(id = R.string.signage_spec_title),
            subtitle = signage.name,
            infols = listOf("너비 : ${signage.width} mm", "높이 : ${signage.height} mm"),
            buttonTitle = "입력",
            isbuttonVisible = true,
            buttonOnclickEvent = navigateToSignageList,
        )
    } else {
        FocusBlock(
            title = stringResource(id = R.string.signage_spec_title),
            infols = listOf(stringResource(id = R.string.need_signage_info)),
            buttonTitle = "입력",
            isbuttonVisible = true,
            buttonOnclickEvent = navigateToSignageList,
        )
    }
}

@Composable // 캐비닛 스펙 틀
fun CabinetSpec(
    cabinet: Cabinet?
) {
    if (cabinet != null) {
        FocusBlock(
            title = stringResource(id = R.string.cabinet_spec_title),
            subtitle = cabinet.name,
            infols = listOf(
                "너비 : ${cabinet.cabinetWidth} mm",
                "높이 : ${cabinet.cabinetHeight} mm",
                "모듈 : ${cabinet.moduleColCount}X${cabinet.moduleRowCount}"
            ),
            buttonTitle = null,
            isbuttonVisible = false,
            buttonOnclickEvent = {},
        )

    } else {
        FocusBlock(
            title = stringResource(id = R.string.cabinet_spec_title),
            infols = listOf(stringResource(id = R.string.need_cabinet_info)),
            buttonTitle = null,
            isbuttonVisible = false,
            buttonOnclickEvent = {},
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

private object RippleCustomTheme : RippleTheme {
    //Your custom implementation...
    @Composable
    override fun defaultColor() =
        RippleTheme.defaultRippleColor(
            Color.Black,
            lightTheme = true
        )

    @Composable
    override fun rippleAlpha(): RippleAlpha =
        RippleTheme.defaultRippleAlpha(
            Color.Black,
            lightTheme = true
        )
}

fun onAppSettingsClosed() {
    // Your code to execute when the Settings activity is closed
    Log.d("gogogo","121212")
}

fun checkAndRequestPermissions(context: Context,mainViewModel:MainViewModel) {
    val permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    val notGrantedPermissions = permissions.filter {
        ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
    }.toTypedArray()

    mainViewModel.permissionsGranted.value = notGrantedPermissions.isEmpty()
}