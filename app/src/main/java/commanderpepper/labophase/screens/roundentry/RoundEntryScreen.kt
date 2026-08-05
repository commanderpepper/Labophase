package commanderpepper.labophase.screens.roundentry

import android.content.ClipData
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.CarouselState
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import commanderpepper.labophase.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import commanderpepper.labophase.models.Leader
import commanderpepper.labophase.models.LOCATIONS_LIST
import commanderpepper.labophase.models.METAS_LIST
import commanderpepper.labophase.models.Meta
import commanderpepper.labophase.models.RoundResult
import commanderpepper.labophase.models.TurnOrder
import commanderpepper.labophase.models.locationById
import commanderpepper.labophase.models.metaById
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import commanderpepper.labophase.screens.roundentry.models.RoundUI
import commanderpepper.labophase.screens.settings.SettingsViewModel
import commanderpepper.labophase.screens.settings.SettingsViewModelImpl
import commanderpepper.labophase.ui.theme.LabophaseTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun RoundEntryScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    entryId: Int? = null,
    roundEntryViewModel: RoundEntryViewModel = koinViewModel<RoundEntryViewModelImpl>(
        key = "round_entry_$entryId",
        parameters = { parametersOf(entryId) }
    ),
    settingsViewModel: SettingsViewModel = koinViewModel<SettingsViewModelImpl>()
) {
    val uiState = roundEntryViewModel.uiState.collectAsStateWithLifecycle()
    val showingDieRoll = settingsViewModel.showingDieRoll.collectAsStateWithLifecycle()
    RoundEntryScreen(
        modifier = modifier,
        onBack = onBack,
        leaderSelectExpanded = entryId == null,
        leaderSelected = uiState.value.leaderSelected,
        playerLeaderList = uiState.value.playerLeaderList,
        roundLeaderList = uiState.value.roundLeaderList,
        rounds = uiState.value.rounds,
        punkRecordEntry = uiState.value.punkRecordEntry,
        showingDieRoll = showingDieRoll.value,
        isLoading = uiState.value.isLoading,
        errorMessage = uiState.value.errorMessage,
        title = uiState.value.title,
        date = uiState.value.date,
        metaId = uiState.value.metaId,
        locationId = uiState.value.locationId,
        addNewRound = roundEntryViewModel::addNewRound,
        transformEntry = roundEntryViewModel::transformEntry,
        chooseLeader = roundEntryViewModel::chooseLeader,
        chooseRoundLeader = roundEntryViewModel::roundLeaderSelect,
        chooseRoundTurnOrder = roundEntryViewModel::roundTurnOrderSelect,
        chooseRoundResult = roundEntryViewModel::roundResultSelect,
        chooseDieRoll = roundEntryViewModel::roundDieRollSelect,
        removeRound = roundEntryViewModel::removeRound,
        onTitleChanged = roundEntryViewModel::setTitle,
        onDateChanged = roundEntryViewModel::setDate,
        onMetaSelected = roundEntryViewModel::setMetaId,
        onLocationSelected = roundEntryViewModel::setLocationId
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoundEntryScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    leaderSelectExpanded: Boolean = true,
    leaderSelected: Leader,
    rounds: List<RoundUI>,
    playerLeaderList: List<Leader>,
    roundLeaderList: List<Leader>,
    punkRecordEntry: String,
    showingDieRoll: Boolean = false,
    isLoading: Boolean,
    errorMessage: String?,
    title: String? = null,
    date: String = LocalDate.now().toString(),
    metaId: Int = Meta.OP16.id,
    locationId: Int? = null,
    addNewRound: () -> Unit,
    transformEntry: () -> Unit,
    chooseLeader: (Leader) -> Unit,
    chooseRoundLeader: (Int, Leader) -> Unit,
    chooseRoundTurnOrder: (Int, TurnOrder) -> Unit,
    chooseRoundResult: (Int, RoundResult) -> Unit,
    chooseDieRoll: (Int, String?) -> Unit = { _, _ -> },
    removeRound: (Int) -> Unit,
    onTitleChanged: (String?) -> Unit = {},
    onDateChanged: (String) -> Unit = {},
    onMetaSelected: (Int) -> Unit = {},
    onLocationSelected: (Int?) -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(leaderSelected.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                }
            )
        }
    ) { innerPadding ->
        WithSharedBorderAngle {
        if (isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) { CircularProgressIndicator() }
        } else if (errorMessage.isNullOrEmpty().not()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) { Text(errorMessage) }
        } else {
            LazyColumn(
                modifier = modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    LeaderPlayerInTournamentSelection(
                        leaderSelected = leaderSelected,
                        leaders = playerLeaderList,
                        rounds = rounds,
                        onLeaderSelected = chooseLeader,
                        initiallyExpanded = leaderSelectExpanded,
                        title = title,
                        date = date,
                        locationId = locationId,
                        expandedContent = {
                            EntryTitleField(title = title, onTitleChanged = onTitleChanged)
                            EntryDateField(date = date, onDateChanged = onDateChanged)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                EntryMetaDropdown(
                                    modifier = Modifier.weight(1f),
                                    metaId = metaId,
                                    onMetaSelected = onMetaSelected
                                )
                                EntryLocationDropdown(
                                    modifier = Modifier.weight(1f),
                                    locationId = locationId,
                                    onLocationSelected = onLocationSelected
                                )
                            }
                        }
                    )
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(onClick = { transformEntry() }) { Text(stringResource(R.string.btn_save_punk_record)) }
                        OutlinedButton(onClick = { addNewRound() }) { Text(stringResource(R.string.btn_new_round)) }
                    }
                }
                if (punkRecordEntry.isNotEmpty()) {
                    item {
                        val punkRecordVisible = rememberSaveable { mutableStateOf(true) }
                        val punkRecordRotation by animateFloatAsState(
                            targetValue = if (punkRecordVisible.value) 180f else 0f,
                            label = "punk_record_rotation"
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { punkRecordVisible.value = !punkRecordVisible.value },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stringResource(R.string.label_punk_record), modifier = Modifier.weight(1f))
                            Icon(
                                Icons.Default.ExpandMore,
                                contentDescription = if (punkRecordVisible.value) stringResource(R.string.cd_hide) else stringResource(R.string.cd_show),
                                modifier = Modifier.rotate(punkRecordRotation)
                            )
                        }
                        AnimatedVisibility(visible = punkRecordVisible.value) {
                            CopyableResult(punkRecordEntry)
                        }
                    }
                }
                items(rounds, key = { it.roundId }) { round ->
                    RoundEntry(
                        round = round,
                        leaders = roundLeaderList,
                        initiallyExpanded = leaderSelectExpanded,
                        leaderSelected = chooseRoundLeader,
                        roundResult = chooseRoundResult,
                        turnOrder = chooseRoundTurnOrder,
                        dieRoll = chooseDieRoll,
                        showingDieRoll = showingDieRoll,
                        removeRound = removeRound
                    )
                }
            }
        }
        } // WithSharedBorderAngle
    }
}

val LocalBorderAngle = compositionLocalOf { 0f }

@Composable
fun WithSharedBorderAngle(content: @Composable () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "border")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = LinearEasing)
        ),
        label = "shared_border_angle"
    )
    CompositionLocalProvider(LocalBorderAngle provides angle) {
        content()
    }
}

@Composable
fun Modifier.animatedBorder(
    colors: List<Color>,
    shape: Shape = CardDefaults.elevatedShape,
    borderWidth: Dp = 2.dp
): Modifier {
    val angle = LocalBorderAngle.current
    return this
        .clip(shape)
        .drawWithContent {
            rotate(degrees = angle) {
                drawCircle(
                    brush = Brush.sweepGradient(colors),
                    radius = size.maxDimension
                )
            }
            drawContent()
        }
        .padding(borderWidth)
}

@Composable
fun LeaderThumbnail(leader: Leader) {
    val colors = if (leader.leaderColors.size == 1) {
        listOf(leader.leaderColors.first().color, Color.White, leader.leaderColors.first().color)
    } else {
        leader.leaderColors.map { it.color }
    }
    ElevatedCard(modifier = Modifier.animatedBorder(colors = colors)) {
        AsyncImage(
            modifier = Modifier.size(48.dp),
            model = "file:///android_asset/leader_thumbnails/${leader.cardId}.webp",
            contentDescription = leader.name
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderPlayerInTournamentSelection(
    leaderSelected: Leader,
    leaders: List<Leader>,
    rounds: List<RoundUI>,
    onLeaderSelected: (Leader) -> Unit,
    initiallyExpanded: Boolean = true,
    title: String? = null,
    date: String = LocalDate.now().toString(),
    locationId: Int? = null,
    expandedContent: @Composable () -> Unit = {}
) {
    val isExpanded = rememberSaveable { mutableStateOf(initiallyExpanded) }
    val carouselState = rememberSaveable(
        leaders.size,
        saver = CarouselState.Saver
    ) { CarouselState { leaders.count() } }
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded.value) 180f else 0f,
        label = "leader_expand_rotation"
    )
    val wins = rounds.count { it.roundResult == RoundResult.Win }
    val losses = rounds.count { it.roundResult == RoundResult.Loss }
    val formattedDate = remember(date) {
        runCatching { LocalDate.parse(date).format(DateTimeFormatter.ofPattern("MMM d, yyyy")) }
            .getOrDefault(date)
    }
    val nameLabel = title ?: leaderSelected.name
    val contextLabel = buildString {
        locationId?.let { locationById(it) }?.let { append(it.abbreviation) }
        if (isNotEmpty()) append(" on $formattedDate") else append("on $formattedDate")
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        ListItem(
            modifier = Modifier.clickable { isExpanded.value = !isExpanded.value },
            leadingContent = {
                Box(modifier = Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
                    LeaderThumbnail(leader = leaderSelected)
                }
            },
            overlineContent = { Text(contextLabel) },
            headlineContent = { Text(nameLabel) },
            supportingContent = { Text("W: $wins - L: $losses") },
            trailingContent = {
                Icon(
                    Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded.value) stringResource(R.string.cd_collapse) else stringResource(R.string.cd_expand),
                    modifier = Modifier.rotate(rotation)
                )
            }
        )
        AnimatedVisibility(visible = isExpanded.value) {
            Column {
                LeaderSelection(
                    leaders = leaders,
                    state = carouselState,
                    onLeaderSelected = onLeaderSelected
                )
                expandedContent()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderSelection(
    leaders: List<Leader>,
    state: CarouselState,
    onLeaderSelected: (Leader) -> Unit
) {
    HorizontalCenteredHeroCarousel(
        state = state,
        maxItemWidth = 128.dp,
        itemSpacing = 8.dp,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) { index ->
        val leader = leaders[index]
        ElevatedCard(modifier = Modifier.maskClip(shape = RoundedCornerShape(corner = CornerSize(2.dp)))) {
            AsyncImage(
                modifier = Modifier.clickable(onClick = { onLeaderSelected(leader) }),
                model = "file:///android_asset/leader_images/${leader.cardId}.webp",
                contentDescription = leader.name
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoundResultSelection(roundResult: RoundResult, onRoundSelected: (RoundResult) -> Unit) {
    SingleChoiceSegmentedButtonRow {
        SegmentedButton(
            selected = roundResult == RoundResult.Win,
            onClick = { onRoundSelected(RoundResult.Win) },
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
        ) { Text(stringResource(R.string.round_result_win)) }
        SegmentedButton(
            selected = roundResult == RoundResult.Loss,
            onClick = { onRoundSelected(RoundResult.Loss) },
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
        ) { Text(stringResource(R.string.round_result_loss)) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TurnOrderSelection(turnOrder: TurnOrder, onTurnOrderSelected: (TurnOrder) -> Unit) {
    SingleChoiceSegmentedButtonRow {
        SegmentedButton(
            selected = turnOrder == TurnOrder.First,
            onClick = { onTurnOrderSelected(TurnOrder.First) },
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
        ) { Text(stringResource(R.string.turn_order_first)) }
        SegmentedButton(
            selected = turnOrder == TurnOrder.Second,
            onClick = { onTurnOrderSelected(TurnOrder.Second) },
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
        ) { Text(stringResource(R.string.turn_order_second)) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DieRollSelection(dieRoll: String?, onDieRollSelected: (String?) -> Unit) {
    SingleChoiceSegmentedButtonRow {
        SegmentedButton(
            selected = dieRoll == "Win",
            onClick = { onDieRollSelected(if (dieRoll == "Win") null else "Win") },
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
        ) { Text("\uD83C\uDFB2") }
        SegmentedButton(
            selected = dieRoll == "Loss",
            onClick = { onDieRollSelected(if (dieRoll == "Loss") null else "Loss") },
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
        ) { Text("❌") }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoundEntry(
    modifier: Modifier = Modifier,
    round: RoundUI,
    leaders: List<Leader>,
    removeRound: (Int) -> Unit,
    leaderSelected: (Int, Leader) -> Unit,
    roundResult: (Int, RoundResult) -> Unit,
    turnOrder: (Int, TurnOrder) -> Unit,
    dieRoll: (Int, String?) -> Unit = { _, _ -> },
    showingDieRoll: Boolean = false,
    initiallyExpanded: Boolean = true
) {
    val isExpanded = rememberSaveable { mutableStateOf(initiallyExpanded) }
    val carouselState = rememberSaveable(
        leaders.size,
        saver = CarouselState.Saver
    ) { CarouselState { leaders.count() } }
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded.value) 180f else 0f,
        label = "round_expand_rotation"
    )
    Column(modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { isExpanded.value = !isExpanded.value }),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LeaderThumbnail(leader = round.leader)
            Spacer(modifier = Modifier.width(8.dp))
            Text(round.summary, modifier = Modifier.weight(1f))
            IconButton(onClick = { isExpanded.value = !isExpanded.value }) {
                Icon(
                    Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded.value) stringResource(R.string.cd_collapse) else stringResource(R.string.cd_expand),
                    modifier = Modifier.rotate(rotation)
                )
            }
            IconButton(onClick = { removeRound(round.roundId) }) {
                Icon(
                    Icons.Default.DeleteForever,
                    contentDescription = stringResource(R.string.action_delete)
                )
            }
        }
        AnimatedVisibility(visible = isExpanded.value) {
            Column(modifier = Modifier.fillMaxWidth()) {
                LeaderSelection(leaders = leaders, state = carouselState) { leader ->
                    leaderSelected(round.roundId, leader)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                    RoundResultSelection(round.roundResult) { result ->
                        roundResult(round.roundId, result)
                    }
                    TurnOrderSelection(round.turnOrder) { order ->
                        turnOrder(round.roundId, order)
                    }
                    if (showingDieRoll) {
                        DieRollSelection(dieRoll = round.dieRoll) { result ->
                            dieRoll(round.roundId, result)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CopyableResult(text: String) {
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = text, modifier = Modifier.weight(1f))
        IconButton(
            onClick = {
                scope.launch {
                    clipboard.setClipEntry(
                        ClipEntry(ClipData.newPlainText(null, text))
                    )
                }
            }
        ) {
            Icon(Icons.Default.ContentCopy, contentDescription = stringResource(R.string.cd_copy))
        }
    }
}

@Composable
private fun EntryTitleField(title: String?, onTitleChanged: (String?) -> Unit) {
    OutlinedTextField(
        value = title ?: "",
        onValueChange = { onTitleChanged(it.ifBlank { null }) },
        label = { Text(stringResource(R.string.label_title)) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EntryDateField(date: String, onDateChanged: (String) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    val parsedDate = remember(date) { runCatching { LocalDate.parse(date) }.getOrNull() }
    val displayDate = parsedDate?.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) ?: date

    OutlinedTextField(
        value = displayDate,
        onValueChange = {},
        readOnly = true,
        label = { Text(stringResource(R.string.label_date)) },
        trailingIcon = {
            IconButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.DateRange, contentDescription = stringResource(R.string.label_date))
            }
        },
        modifier = Modifier.fillMaxWidth()
    )

    if (showDialog) {
        val initialMillis = parsedDate
            ?.atStartOfDay(ZoneOffset.UTC)
            ?.toInstant()
            ?.toEpochMilli()
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selected = Instant.ofEpochMilli(millis)
                            .atOffset(ZoneOffset.UTC)
                            .toLocalDate()
                        onDateChanged(selected.toString())
                    }
                    showDialog = false
                }) { Text(stringResource(android.R.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(android.R.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EntryMetaDropdown(
    modifier: Modifier = Modifier,
    metaId: Int,
    onMetaSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val currentMeta = metaById(metaId)
    ExposedDropdownMenuBox(modifier = modifier, expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = currentMeta?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.label_meta)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            METAS_LIST.forEach { meta ->
                DropdownMenuItem(
                    text = { Text(meta.name) },
                    onClick = { onMetaSelected(meta.id); expanded = false },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EntryLocationDropdown(
    modifier: Modifier = Modifier,
    locationId: Int?,
    onLocationSelected: (Int?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val currentLocation = locationId?.let { locationById(it) }
    ExposedDropdownMenuBox(modifier = modifier, expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = currentLocation?.abbreviation ?: stringResource(R.string.label_none),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.label_location)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.label_none)) },
                onClick = { onLocationSelected(null); expanded = false },
                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
            )
            LOCATIONS_LIST.forEach { location ->
                DropdownMenuItem(
                    text = { Text(location.name) },
                    onClick = { onLocationSelected(location.id); expanded = false },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

private val previewLeaders = listOf(Leader.PBLuffy, Leader.RShanks, Leader.GZoro)
private val previewRound1 = RoundUI(
    roundId = 1,
    leader = Leader.PBLuffy,
    summary = "PB Luffy, W, 1",
    roundResult = RoundResult.Win,
    turnOrder = TurnOrder.First
)
private val previewRound2 = RoundUI(
    roundId = 2,
    leader = Leader.RShanks,
    summary = "R Shanks, L, 2",
    roundResult = RoundResult.Loss,
    turnOrder = TurnOrder.Second
)
private val previewPunkRecord = "!PR add\n" +
        "UG Luffy\n" +
        "W G Bonney 2nd\n" +
        "W UY Nami 1st\n" +
        "W G Mihawk 1st\n" +
        "W UY Nami 1st"

@Preview(showBackground = true)
@Composable
private fun PreviewRoundEntryScreen() {
    LabophaseTheme {
        RoundEntryScreen(
            onBack = {},
            leaderSelected = Leader.PBLuffy,
            rounds = listOf(previewRound1, previewRound2),
            playerLeaderList = previewLeaders,
            roundLeaderList = previewLeaders,
            punkRecordEntry = previewPunkRecord,
            showingDieRoll = true,
            isLoading = false,
            errorMessage = null,
            addNewRound = {},
            transformEntry = {},
            chooseLeader = {},
            chooseRoundLeader = { _, _ -> },
            chooseRoundTurnOrder = { _, _ -> },
            chooseRoundResult = { _, _ -> },
            removeRound = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewRoundEntryScreenEmpty() {
    LabophaseTheme {
        RoundEntryScreen(
            onBack = {},
            leaderSelected = Leader.PBLuffy,
            rounds = emptyList(),
            playerLeaderList = previewLeaders,
            roundLeaderList = previewLeaders,
            punkRecordEntry = "",
            showingDieRoll = true,
            isLoading = false,
            errorMessage = null,
            addNewRound = {},
            transformEntry = {},
            chooseLeader = {},
            chooseRoundLeader = { _, _ -> },
            chooseRoundTurnOrder = { _, _ -> },
            chooseRoundResult = { _, _ -> },
            removeRound = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewRoundEntryScreenLoading() {
    LabophaseTheme {
        RoundEntryScreen(
            onBack = {},
            leaderSelected = Leader.PBLuffy,
            rounds = emptyList(),
            playerLeaderList = emptyList(),
            roundLeaderList = emptyList(),
            punkRecordEntry = "",
            isLoading = true,
            errorMessage = null,
            addNewRound = {},
            transformEntry = {},
            chooseLeader = {},
            chooseRoundLeader = { _, _ -> },
            chooseRoundTurnOrder = { _, _ -> },
            chooseRoundResult = { _, _ -> },
            removeRound = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewRoundEntryScreenError() {
    LabophaseTheme {
        RoundEntryScreen(
            onBack = {},
            leaderSelected = Leader.PBLuffy,
            rounds = emptyList(),
            playerLeaderList = emptyList(),
            roundLeaderList = emptyList(),
            punkRecordEntry = "",
            isLoading = false,
            errorMessage = "Something went wrong",
            addNewRound = {},
            transformEntry = {},
            chooseLeader = {},
            chooseRoundLeader = { _, _ -> },
            chooseRoundTurnOrder = { _, _ -> },
            chooseRoundResult = { _, _ -> },
            removeRound = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewLeaderPlayerInTournamentSelection() {
    LabophaseTheme {
        LeaderPlayerInTournamentSelection(
            leaderSelected = Leader.PBLuffy,
            leaders = previewLeaders,
            rounds = listOf(previewRound1, previewRound2),
            onLeaderSelected = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun PreviewLeaderSelection() {
    LabophaseTheme {
        val state = remember { CarouselState { previewLeaders.count() } }
        LeaderSelection(leaders = previewLeaders, state = state, onLeaderSelected = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewRoundEntry() {
    LabophaseTheme {
        RoundEntry(
            round = previewRound1,
            leaders = previewLeaders,
            leaderSelected = { _, _ -> },
            roundResult = { _, _ -> },
            turnOrder = { _, _ -> },
            removeRound = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewRoundResultSelection() {
    LabophaseTheme {
        RoundResultSelection(roundResult = RoundResult.Win, onRoundSelected = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTurnOrderSelection() {
    LabophaseTheme {
        TurnOrderSelection(turnOrder = TurnOrder.First, onTurnOrderSelected = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewCopyableResult() {
    LabophaseTheme {
        CopyableResult(text = previewPunkRecord)
    }
}
