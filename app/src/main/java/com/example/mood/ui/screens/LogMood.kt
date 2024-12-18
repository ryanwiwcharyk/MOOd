package com.example.mood.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mood.localNavController
import com.example.mood.model.MoodHistory
import com.example.mood.model.UserMood
import com.example.mood.model.enums.MoodTypeEnum
import com.example.mood.ui.NavBar
import com.example.mood.ui.TopBar
import com.example.mood.viewmodel.MoodViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter


@Composable
fun LogMoodScreen(contentPadding: PaddingValues, moodViewModel: MoodViewModel) {
    val navController = localNavController.current

    Box(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background))
    {
        Column(
            modifier = Modifier
                .padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top // Align items at the top
        ) {
            TopBar { navController.navigate("UserAccount") }
            NavBar(
                onHomeClick = { navController.navigate("HomeScreen") },
                onLogClick = { navController.navigate("LogMood") }
            )
            LogMood(moodViewModel)
        }
    }
}

@Composable
fun LogMood(moodViewModel: MoodViewModel) {
    Column {
        MoodSelectionPage(moodViewModel)
    }
}

@Composable
fun MoodSelectionPage(moodViewModel: MoodViewModel) {
    var selectedMood by remember { mutableStateOf<MoodTypeEnum?>(null) }
    var thoughts by remember { mutableStateOf("") }
    var moodTypesEnums by remember { mutableStateOf<List<MoodTypeEnum>>(emptyList()) }
    val rememberCoroutineScope = rememberCoroutineScope()
    val navController = localNavController.current


    LaunchedEffect(Unit) {
        moodTypesEnums = moodViewModel.getAllMoodTypes()
    }


    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "How are you feeling today?",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(24.dp),
            color = MaterialTheme.colorScheme.onSurface
        )

        MoodTypeDropdown(
            moodTypes = moodTypesEnums,
            selectedMoodType = selectedMood,
            onMoodTypeSelected = { selectedMood = it }
        )

        OutlinedTextField(
            value = thoughts,
            onValueChange = { thoughts = it },
            label = { Text("Any thoughts?",
                color = MaterialTheme.colorScheme.onSurface) },
            placeholder = { Text("Type your thoughts here...") },
            modifier = Modifier.fillMaxWidth()
                .padding(24.dp)
        )

        // Submit Button
        Button(
            onClick = {
                if (selectedMood != null) {
                    rememberCoroutineScope.launch {
                        navController.navigate("HomeScreen")
                        moodViewModel.logMood(selectedMood!!, thoughts)
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor =  MaterialTheme.colorScheme.primary,
                contentColor = Color.Black
            ),
            modifier = Modifier.
            width(150.dp)
        ) {
            Text("Log Mood")
        }

        Text(
            text = "Your mood history:",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(10.dp),
            color = MaterialTheme.colorScheme.onSurface
        )
        MoodLegend()
        MoodCalendar(moodViewModel)
    }
}

@SuppressLint("NewApi")
@Composable
fun MoodCalendar(moodViewModel: MoodViewModel) {

    var moodsInCalendar by remember { mutableStateOf<List<MoodHistory>>(emptyList()) }

    LaunchedEffect(Unit) {
         moodsInCalendar = moodViewModel.getMoodDateAndTypeFromUserId()
    }



    val currentMonth = YearMonth.now()
    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
    val daysInMonth = currentMonth.lengthOfMonth()
    val datesInMonth = (1..daysInMonth).map { currentMonth.atDay(it) }
    val moodType = remember { mutableStateOf<MoodTypeEnum?>(null) }

    Text(text = currentMonth.format(formatter), color = MaterialTheme.colorScheme.onSurface)

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        contentPadding = PaddingValues(8.dp)
    ) {

        items(datesInMonth) { date ->
            moodType.value = moodsInCalendar?.find { it.dateLogged.toLocalDate() == date }?.userMoodId?.let { moodId ->
                MoodTypeEnum.entries.find { it.id == moodId }
            }
            MoodDayItem(date, moodType.value)
        }
    }
}


@SuppressLint("NewApi")
@Composable
fun MoodDayItem(date: LocalDate, mood: MoodTypeEnum?) {
    val moodColor = when (mood) {
        MoodTypeEnum.HAPPY -> Color.Green
        MoodTypeEnum.SAD -> Color.Blue
        MoodTypeEnum.NEUTRAL -> Color.Gray
        MoodTypeEnum.ANGRY -> Color.Red
        MoodTypeEnum.ANXIOUS-> Color.Yellow
        MoodTypeEnum.CALM-> Color.LightGray
        MoodTypeEnum.EXCITED-> Color.Cyan
        MoodTypeEnum.CONFUSED-> Color.Magenta
        else -> MaterialTheme.colorScheme.tertiary
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .padding(4.dp)
            .background(moodColor, shape = CircleShape),
        contentAlignment = Alignment.Center,

    ) {
        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodTypeDropdown(
    moodTypes:  List<MoodTypeEnum>,
    selectedMoodType: MoodTypeEnum?,
    onMoodTypeSelected: (MoodTypeEnum) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(selectedMoodType?.mood ?: "Select Mood") }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = { },
            readOnly = true,
            label = { Text("Mood Type") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            moodTypes.forEach { moodType ->
                DropdownMenuItem(
                    text = { Text(moodType.mood) },
                    onClick = {
                        selectedText = moodType.mood
                        onMoodTypeSelected(moodType)
                        expanded = false
                    }
                )
            }
        }
    }
}
@Composable
fun MoodLegend() {
    val moods = listOf(
        MoodTypeEnum.HAPPY to Color.Green,
        MoodTypeEnum.SAD to Color.Blue,
        MoodTypeEnum.NEUTRAL to Color.Gray,
        MoodTypeEnum.ANGRY to Color.Red,
        MoodTypeEnum.ANXIOUS to Color.Yellow,
        MoodTypeEnum.CALM to Color.LightGray,
        MoodTypeEnum.EXCITED to Color.Cyan,
        MoodTypeEnum.CONFUSED to Color.Magenta
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        items(moods.size) { index ->
            val (mood, color) = moods[index]

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .background(color, shape = CircleShape)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = mood.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

