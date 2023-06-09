package com.signez.signageproblemshooting.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.signez.signageproblemshooting.ui.theme.SignEzTheme

/**
 *
 */
@Composable
fun FocusBlock(
    title: String,
    subtitle: String? = null,
    infols: List<String>? = null,
    buttonTitle: String?,
    isbuttonVisible: Boolean,
    buttonOnclickEvent: () -> Unit,
) {
    Card(
        modifier = Modifier
            .padding(top = 8.dp, bottom = 8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colors.surface
        )
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.h3,
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier
                        .padding(start = 18.dp, top = 8.dp)
                        .weight(0.4f)
                )
                if (buttonTitle != null) {
                    Spacer(
                        modifier = Modifier.weight(0.3f)
                    )
                    Column(
                        modifier = Modifier.weight(0.4f)
                    ) {
                        InFocusBlockButton(
                            title = buttonTitle,
                            isVisible = isbuttonVisible,
                            onClickEvent = buttonOnclickEvent
                        )
                    }
                } else {
                    Spacer(
                        modifier = Modifier.weight(0.3f)
                    )
                    Column(
                        modifier = Modifier.weight(0.4f)
                    ) {
                        InFocusBlockButton(
                            title = "버튼없어요",
                            isVisible = isbuttonVisible,
                            onClickEvent = buttonOnclickEvent
                        )
                    }
                }

            }

            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.h4,
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier.padding(start = 18.dp, top = 8.dp, bottom = 8.dp)
                )
            }

            if (infols != null) {
                for (info in infols) {
                    Text(
                        text = info,
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.onBackground,
                        modifier = Modifier.padding(start = 18.dp, top = 4.dp, bottom = 4.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(15.dp))
    }
}

@Composable
fun WhiteButton(
    title: String,
    isUsable: Boolean,
    onClickEvent: () -> Unit
) {
    if (isUsable) {
        androidx.compose.material3.Button(
            onClick = onClickEvent,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, MaterialTheme.colors.secondaryVariant),
            enabled = isUsable,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colors.surface,
                disabledContainerColor = MaterialTheme.colors.surface,
            ),
            modifier = Modifier
                .padding(top = 2.dp, bottom = 2.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.button,
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }
    } else {
        androidx.compose.material3.Button(
            onClick = onClickEvent,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, MaterialTheme.colors.secondaryVariant),
            enabled = isUsable,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colors.surface,
                disabledContainerColor = MaterialTheme.colors.surface,
            ),
            modifier = Modifier
                .padding(top = 2.dp, bottom = 2.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.button,
                color = MaterialTheme.colors.onBackground,
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }
    }
}

@Composable
fun InFocusBlockButton(
    title: String,
    isVisible: Boolean,
    onClickEvent: () -> Unit
) {
    if (isVisible) {
        androidx.compose.material3.Button(
            onClick = onClickEvent,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, MaterialTheme.colors.secondaryVariant),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colors.surface,
                disabledContainerColor = MaterialTheme.colors.surface,
            ),
            modifier = Modifier
                .padding(top = 10.dp, end = 15.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }
    } else {
        androidx.compose.material3.Button(
            onClick = {},
            shape = RoundedCornerShape(20.dp),
            enabled = false,
            border = BorderStroke(1.dp, MaterialTheme.colors.surface),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colors.surface,
                disabledContainerColor = MaterialTheme.colors.surface,
            ),
            modifier = Modifier
                .padding(top = 10.dp, end = 15.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.surface,
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }
    }
}

@Composable
fun IntentButton(
    title: String,
    onClickEvent: () -> Unit
) {
    androidx.compose.material3.Button(
        onClick = onClickEvent,
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colors.primaryVariant,
        ),
        modifier = Modifier
            .padding(start = 15.dp, end = 15.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier.padding(bottom = 2.dp)
        )
    }
}

@Composable
fun SignEzFloatingButton(
    onClickEvent: () -> Unit
) {
    FloatingActionButton(
        onClick = onClickEvent,
        shape = CircleShape,
        containerColor = MaterialTheme.colors.primary,
    ) {
        androidx.compose.material.Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = "추가",
            tint = MaterialTheme.colors.onPrimary
        )
    }
}

@Composable
fun TutorialStartButton(
    title: String,
    onClickEvent: () -> Unit
) {
    androidx.compose.material3.Button(
        onClick = onClickEvent,
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colors.primary,
        ),
        modifier = Modifier
            .padding(start = 8.dp, end = 8.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onPrimary,
            modifier = Modifier.padding(bottom = 2.dp)
        )
    }
}


@Composable
fun BottomSingleFlatButton(
    title: String,
    isUsable: Boolean,
    onClickEvent: () -> Unit
) {

    androidx.compose.material3.Button(
        onClick = onClickEvent,
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colors.background,
            disabledContainerColor = MaterialTheme.colors.background,
        ),
        enabled = isUsable,
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.button,
            color = if (isUsable) MaterialTheme.colors.onSurface else MaterialTheme.colors.onBackground,
            modifier = Modifier.padding(bottom = 2.dp)
        )
    }
}

@Composable
fun BottomDoubleFlatButton(
    leftTitle: String,
    rightTitle: String,
    isLeftUsable: Boolean,
    isRightUsable: Boolean,
    leftOnClickEvent: () -> Unit,
    rightOnClickEvent: () -> Unit,
) {
    Row() {
        androidx.compose.material3.Button(
            onClick = leftOnClickEvent,
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colors.background,
            ),
            enabled = isLeftUsable,
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp)
                .weight(1f)
        ) {
            if (isLeftUsable) {
                Text(
                    text = leftTitle,
                    style = MaterialTheme.typography.button,
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            } else {
                Text(
                    text = leftTitle,
                    style = MaterialTheme.typography.button,
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
        }
        androidx.compose.material3.Button(
            onClick = rightOnClickEvent,
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colors.background,
                disabledContainerColor = MaterialTheme.colors.background,
            ),
            enabled = isRightUsable,
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp)
                .weight(1f),
        ) {
            if (isRightUsable) {
                Text(
                    text = rightTitle,
                    style = MaterialTheme.typography.button,
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            } else {
                Text(
                    text = rightTitle,
                    style = MaterialTheme.typography.button,
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
        }
    }

}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ResultHistoryBlock(
    site: String = "",
    date: String = "",
    thumbnail: ByteArray? = null,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colors.surface
        )
    ) {
        //
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                thumbnail?.let { byteArray ->
                    GlideImage(
                        model = byteArray,
                        contentDescription = "글라이드",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(color = Color.Black)
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = site,
                    style = MaterialTheme.typography.h4,
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier.padding(start = 10.dp, bottom = 5.dp)
                )

            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = date,
                    style = MaterialTheme.typography.h4,
                    color = MaterialTheme.colors.onSecondary,
                    modifier = Modifier.padding(start = 10.dp, bottom = 5.dp),
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(10.dp))
}


@Composable
fun DotsIndicator(
    totalDots: Int,
    selectedIndex: Int
) {

    LazyRow(
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center
    ) {

        items(totalDots) { index ->
            if (index == selectedIndex) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(color = Color(0xFF0F429D))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(color = Color(0xFFB3CCF8))
                )
            }

            if (index != totalDots - 1) {
                Spacer(modifier = Modifier.padding(horizontal = 2.dp))
            }
        }
    }
}

@Composable
fun BottomTutorialFlatButton(
    leftTitle: String,
    rightTitle: String,
    isLeftUsable: Boolean,
    isRightUsable: Boolean,
    totalDots: Int,
    selectedIndex: Int,
    leftOnClickEvent: () -> Unit,
    rightOnClickEvent: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.material3.Button(
            onClick = leftOnClickEvent,
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colors.background,
                disabledContainerColor = MaterialTheme.colors.background,
            ),
            enabled = isLeftUsable,
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp)
                .weight(1f)
        ) {
            if (isLeftUsable) {
                Text(
                    text = leftTitle,
                    style = MaterialTheme.typography.button,
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            } else {
                Text(
                    text = leftTitle,
                    style = MaterialTheme.typography.button,
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
        }
        DotsIndicator(totalDots = totalDots, selectedIndex = selectedIndex)
        androidx.compose.material3.Button(
            onClick = rightOnClickEvent,
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colors.background,
                disabledContainerColor = MaterialTheme.colors.background,
            ),
            enabled = isRightUsable,
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp)
                .weight(1f),
        ) {
            if (isRightUsable) {
                Text(
                    text = rightTitle,
                    style = MaterialTheme.typography.button,
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            } else {
                Text(
                    text = rightTitle,
                    style = MaterialTheme.typography.button,
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
        }
    }

}

@Preview
@Composable
fun ComponentPreview() {
    SignEzTheme(darkTheme = false) {
        Column() {
        }
    }
}

