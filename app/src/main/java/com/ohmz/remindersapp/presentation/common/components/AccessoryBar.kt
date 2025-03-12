import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ohmz.remindersapp.domain.model.ReminderAction
import com.ohmz.remindersapp.presentation.common.components.DateSelector
import java.util.Calendar
import java.util.Date
import androidx.compose.foundation.layout.*
import androidx.compose.animation.*
import androidx.compose.material.icons.filled.*
import com.ohmz.remindersapp.domain.model.Priority
import com.ohmz.remindersapp.presentation.common.components.PrioritySelector

/**
 * Utility function to get the current date with time set to midnight
 */
private fun Date.atMidnight(): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}

@Composable
fun AccessoryBar(
    selectedAction: ReminderAction?,
    onActionSelected: (ReminderAction) -> Unit,
    modifier: Modifier = Modifier,
    onTodaySelected: () -> Unit = {},
    onTomorrowSelected: () -> Unit = {},
    onWeekendSelected: () -> Unit = {},
    onDateTimeSelected: () -> Unit = {},
    onLowPrioritySelected: () -> Unit = {},
    onMediumPrioritySelected: () -> Unit = {},
    onHighPrioritySelected: () -> Unit = {},
    hasDate: Boolean = false, // Parameter to indicate if a date is set
    dueDate: Date? = null, // Due date parameter to pass to DateSelector
    currentPriority: Priority = Priority.MEDIUM // Current priority for highlighting
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .imePadding() // This ensures the bar stays above the keyboard
            .windowInsetsPadding(WindowInsets.navigationBars) // For navigation bar
            .wrapContentHeight() // Force the bar to wrap its content
    ) {
        // Animated priority selector with sliding effect
        AnimatedVisibility(
            visible = selectedAction == ReminderAction.TAG,
            enter = slideInVertically(
                initialOffsetY = { -it }, // Slide in from above
                animationSpec = tween(durationMillis = 500)
            ) + expandVertically(
                expandFrom = Alignment.Top,
                animationSpec = tween(durationMillis = 500)
            ) + fadeIn(
                animationSpec = tween(durationMillis = 500)
            ),
            exit = slideOutVertically(
                targetOffsetY = { -it }, // Slide out to above
                animationSpec = tween(durationMillis = 600)
            ) + shrinkVertically(
                shrinkTowards = Alignment.Top,
                animationSpec = tween(durationMillis = 600)
            ) + fadeOut(
                animationSpec = tween(durationMillis = 600)
            )
        ) {
            PrioritySelector(
                onLowPrioritySelected = onLowPrioritySelected,
                onMediumPrioritySelected = onMediumPrioritySelected,
                onHighPrioritySelected = onHighPrioritySelected,
                currentPriority = currentPriority
            )
        }

        // Animated date selector with sliding effect
        AnimatedVisibility(
            visible = selectedAction == ReminderAction.CALENDAR,
            enter = slideInVertically(
                initialOffsetY = { -it }, // Slide in from above
                animationSpec = tween(durationMillis = 500)
            ) + expandVertically(
                expandFrom = Alignment.Top,
                animationSpec = tween(durationMillis = 500)
            ) + fadeIn(
                animationSpec = tween(durationMillis = 500)
            ),
            exit = slideOutVertically(
                targetOffsetY = { -it }, // Slide out to above
                animationSpec = tween(durationMillis = 600)
            ) + shrinkVertically(
                shrinkTowards = Alignment.Top,
                animationSpec = tween(durationMillis = 600)
            ) + fadeOut(
                animationSpec = tween(durationMillis = 600)
            )
        ) {
            DateSelector(
                onTodaySelected = onTodaySelected,
                onTomorrowSelected = onTomorrowSelected,
                onNextWeekendSelected = onWeekendSelected,
                onDateTimeSelected = onDateTimeSelected,
                currentDate = dueDate
            )
        }

        // Main action bar with icons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .background(Color.White),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Calendar icon - now colored only if hasDate is true (not just when selected)
            IconButton(
                onClick = { onActionSelected(ReminderAction.CALENDAR) },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Calendar",
                    tint = if (hasDate) Color(0xFF007AFF) else Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            }

            // List icon (replaced location icon)
            IconButton(
                onClick = { onActionSelected(ReminderAction.LOCATION) }, // Keep using LOCATION action for backward compatibility
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = "List",
                    tint = if (selectedAction == ReminderAction.LOCATION)
                        Color(0xFF007AFF) else Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Tag/Priority icon
            IconButton(
                onClick = { onActionSelected(ReminderAction.TAG) },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Priority",
                    tint = if (selectedAction == ReminderAction.TAG)
                        Color(0xFF007AFF) else Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Favorite icon
            IconButton(
                onClick = { onActionSelected(ReminderAction.FAVORITE) },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = if (selectedAction == ReminderAction.FAVORITE)
                        Icons.Default.Favorite
                    else
                        Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (selectedAction == ReminderAction.FAVORITE)
                        Color(0xFF007AFF) else Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Person/Photo icon
            IconButton(
                onClick = { onActionSelected(ReminderAction.CAMERA) },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBox,
                    contentDescription = "Photo",
                    tint = if (selectedAction == ReminderAction.CAMERA)
                        Color(0xFF007AFF) else Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}