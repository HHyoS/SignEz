package com.signez.signageproblemshooting.ui.signage

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class BlockViewModel : ViewModel() {
    val blocks = mutableStateListOf<Block>()

    var idx = mutableStateOf(0)
    var firstRowLength = mutableStateOf(0)
    var fixed = mutableStateOf(false)
    var finish = mutableStateOf(false)

    fun addBlock(
        width:Int,
        height:Int,
        widthLimit: Int,
        heightLimit:Int,
        colModuleCount: Int,
        rowModuleCount: Int,
    ) : Boolean{
        val newBlock =  Block(
            id = blocks.size,
            width = width,
            height = height,
            colModuleCount = colModuleCount,
            rowModuleCount = rowModuleCount
        )
        if (blocks.size < 1 && width <= widthLimit && height <= heightLimit) {
            newBlock.x = 0
            newBlock.y = 0
            newBlock.gridX = 0
            newBlock.gridY = 0
            blocks.add(newBlock)
            return true // 블록 추가 성공
        } else {
            // 마지막 블록의 시작 위치 + 그 블록의 width + 새로 넣을 블록의 width 가 limit 을 초과하면
            if (blocks.last().x + blocks.last().width + width > widthLimit) {
                // 행을 새로 추가할 때 높이 제한 초과 여부 체크
                if (blocks.last().y + blocks.last().height + height <= heightLimit
                    && blocks[0].width == width) {
                    idx.value = 0 // 줄 바꿈으로 초기화
                    newBlock.x = 0 // 첫 열 부터 다시 시작
                    newBlock.y = blocks.last().y + blocks.last().height
                    newBlock.gridX = 0
                    //ex) 0,0 시작 하는 블록이 위에있고 그 블록 높이가 3이면. 아래 블록 시작 좌표는 0 + 3 이다.
                    newBlock.gridY = blocks.last().gridY + blocks.last().rowModuleCount
                    blocks.add(newBlock)
                    fixed.value = true
                    return true // 블록 추가 성공
                }
                finish.value = true // 꽉 참
                return false // 블록 추가 실페
            } else { // 같은 줄 추가해 나가는 중에는 높이가 같아야하고 윗 블록과 넓이도 같아야함.
                if (blocks.last().height == height) {
                    // 둘째 줄 부터는 첫 줄과 비교 해야 한다.
                    idx.value += 1
                    if (fixed.value && blocks[idx.value].width != width) { return false }

                    newBlock.x = blocks.last().x + blocks.last().width // 옆 으로 채워나감.
                    newBlock.y = blocks.last().y // 높이 유지
                    newBlock.gridX = blocks.last().gridX + blocks.last().colModuleCount
                    //ex) 한줄에서 추가 되는 동안 높이는 그대로
                    newBlock.gridY = blocks.last().gridY //
                    blocks.add(newBlock)
                    if ( !fixed.value ) { firstRowLength.value += 1 } // 기준 행 마지막 인덱스 갱신.
                    return true
                }
                return false
            }
        }
    }

    fun updateBlock(block: Block) {
        val index = blocks.indexOfFirst { it.id == block.id }
        if (index != -1) {
            blocks[index] = block
        }
    }

    fun clear() {
        idx.value = 0
        firstRowLength.value = 0
        fixed.value = false
        blocks.clear()
    }

    fun cancel() {
        fixed.value = false
        blocks.removeLastOrNull()
        idx.value -= 1
        // 음수가 되었을 때 만 다루면 됨.
        if (idx.value < 0) {
            if (fixed.value) {
                // 첫 행꽉 채운 상태에서 하나 빠진거면
                if ( blocks.size-1 == firstRowLength.value ) {
                    fixed.value = false // 고정 상태 해제
                } else {
                    idx.value = firstRowLength.value //기준 행 길이로 맞춰줌.
                }
            }
            // 아직 기준 행 생성 전
            else {
                idx.value = 0
            }
        }
        if (!fixed.value && blocks.size > 0) {
            firstRowLength.value = blocks.size-1
            idx.value = firstRowLength.value // 새로 생성시 열 위치
        }

        Log.d("chch","${idx.value} ${firstRowLength.value}")
    }


}
data class Block(
    val id: Int,
    val width: Int, // 가로
    val height: Int, // 세로
    var x: Int = 0, // 시작 x 위치
    var y: Int = 0, // 시작 y 위치
    val colModuleCount: Int, // 열 모듈 수
    val rowModuleCount: Int, // 행 보듈 수
    var gridX:Int=0, // 격자상 시작 x 좌표
    var gridY:Int=0 // 격자상 시작 y 좌표
)

