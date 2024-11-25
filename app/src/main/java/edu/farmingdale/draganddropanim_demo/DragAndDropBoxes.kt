@file:OptIn(ExperimentalFoundationApi::class)

package edu.farmingdale.draganddropanim_demo

import android.content.ClipData
import android.content.ClipDescription
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

@Composable
fun DragAndDropBoxes(modifier: Modifier = Modifier) {
    var isPlaying by remember { mutableStateOf(true) }
    var isDroppedUp by remember { mutableStateOf(false) } // Track if dropped up or down
    var dragBoxIndex by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {

        Row(
            modifier = modifier
                .fillMaxWidth()
                .weight(0.2f)
        ) {
            val boxCount = 4

            repeat(boxCount) { index ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(10.dp)
                        .border(1.dp, Color.Black)
                        .dragAndDropTarget(
                            shouldStartDragAndDrop = { event ->
                                event.mimeTypes().contains(ClipDescription.MIMETYPE_TEXT_PLAIN)
                            },
                            target = remember {
                                object : DragAndDropTarget {
                                    override fun onDrop(event: DragAndDropEvent): Boolean {
                                        // Handle the drop event and toggle state for animation
                                        isPlaying = !isPlaying
                                        dragBoxIndex = index
                                        // Determine if dropped at the upper half or lower half
                                        //isDroppedUp = event.position.y < 500
                                        return true
                                    }
                                }
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    this@Row.AnimatedVisibility(
                        visible = index == dragBoxIndex,
                        enter = scaleIn() + fadeIn(),
                        exit = scaleOut() + fadeOut()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = "Icon",
                            tint = Color.Red,
                            modifier = Modifier
                                .fillMaxSize()
                                .dragAndDropSource {
                                    detectTapGestures(
                                        onLongPress = { offset ->
                                            startTransfer(
                                                transferData = DragAndDropTransferData(
                                                    clipData = ClipData.newPlainText(
                                                        "text", ""
                                                    )
                                                )
                                            )
                                        }
                                    )
                                }
                        )
                    }
                }
            }
        }

        // Animation for translation (offset) and rotation
        val targetOffset by animateIntOffsetAsState(
            targetValue = if (isPlaying) {
                if (isDroppedUp) IntOffset(100, 50) else IntOffset(200, 300)
            } else {
                IntOffset(50, 100) // Default position when not playing
            },
            animationSpec = tween(durationMillis = 3000, easing = LinearEasing)
        )

        val rotationValue by animateFloatAsState(
            targetValue = if (isPlaying) 360f else 0f,
            animationSpec = repeatable(
                iterations = if (isPlaying) 10 else 1,
                tween(durationMillis = 3000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )

        // Box that moves and rotates based on drop event
        Box(
            modifier = Modifier
                .size(100.dp)
                .offset { targetOffset } // Apply horizontal and vertical translation
                .background(Color.Green)
                .rotate(rotationValue), // Rotate the box
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Face,
                contentDescription = "Face",
                modifier = Modifier
                    .padding(10.dp)
            )
        }

        // Reset button to center the box
        Button(
            onClick = {
                // Reset to the center of the screen
                isPlaying = true
                isDroppedUp = false
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 20.dp)
        ) {
            Text(text = "Reset Position")
        }
    }
}
