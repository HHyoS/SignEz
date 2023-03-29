package com.signez.signageproblemshooting.ui.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.signez.signageproblemshooting.R
import com.signez.signageproblemshooting.data.entities.Cabinet
import com.signez.signageproblemshooting.data.entities.Signage
import com.signez.signageproblemshooting.ui.components.FocusBlock
import com.signez.signageproblemshooting.ui.components.WhiteButton
import com.signez.signageproblemshooting.ui.inputs.MainViewModel
import com.signez.signageproblemshooting.ui.theme.SignEzTheme

@Composable //지난 분석 결과 틀
fun PastResult(
    modifier: Modifier = Modifier,
) {
    val itemList = listOf<Painter>(
        painterResource(id = R.drawable.bluesign),
        painterResource(id = R.drawable.bluesign),
        painterResource(id = R.drawable.bluesign),
        painterResource(id = R.drawable.bluesign),
        painterResource(id = R.drawable.bluesign),
        painterResource(id = R.drawable.bluesign),
        painterResource(id = R.drawable.bluesign)
    )
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
//                        .align(alignment = Alignment.Top),
                    style = MaterialTheme.typography.bodyLarge
                )
                androidx.compose.material.IconButton(onClick = {}) {
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
                        Image(
                            painter = item,
                            contentDescription = "아이템",
                            modifier = Modifier
                                .width(100.dp)
                                .height(100.dp)
//                                .padding(5.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { /* 해당 분석 결과로 가는 이벤트 */ }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.padding(10.dp))
        } // Col 끝
    }
}

@Preview
@Composable
fun ComponentPreview() {
    SignEzTheme(darkTheme = false) {
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
            subtitle = signage.name,
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

@Composable
fun AppSettingsScreen(mainViewModel: MainViewModel) {
    val context = LocalContext.current

    val openAppSettingsLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
        mainViewModel.onAppSettingsResult()
    }

    val navigateToAppSettings = mainViewModel.navigateToAppSettings

    if (navigateToAppSettings.value) {
        openAppSettings(context, openAppSettingsLauncher)
    }
    // Your other composables
}

fun openAppSettings(context: Context, appSettingsResultLauncher: ActivityResultLauncher<Intent>) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts("package", context.packageName, null)
    intent.data = uri
    appSettingsResultLauncher.launch(intent)
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

@Composable
fun PermissionInfo() {
    MaterialTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "정상적인 앱 사용을 위해서")
            Text(text = "아래와 같은 권한이 필요합니다.")
            Text(text = "- 카메라 권한")
            Text(text = " : 사이니지 촬영\n")
            Text(text = "- 파일 및 미디어 접근 권한")
            Text(text = " : 디바이스의 사이니지 사진/영상 자료 불러오기")
        }
    }
}