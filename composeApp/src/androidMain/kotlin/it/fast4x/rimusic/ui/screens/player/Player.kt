package it.fast4x.rimusic.ui.screens.player

import android.annotation.SuppressLint
import android.graphics.RenderEffect
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.compose.ui.util.fastZip
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import androidx.core.graphics.ColorUtils.colorToHSL
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import androidx.palette.graphics.Palette
import app.kreate.android.Preferences
import app.kreate.android.R
import app.kreate.android.drawable.APP_ICON_IMAGE_BITMAP
import app.kreate.android.screens.player.background.BlurredCover
import app.kreate.android.themed.rimusic.screen.player.ActionBar
import com.mikepenz.hypnoticcanvas.shaderBackground
import com.mikepenz.hypnoticcanvas.shaders.BlackCherryCosmos
import com.mikepenz.hypnoticcanvas.shaders.GlossyGradients
import com.mikepenz.hypnoticcanvas.shaders.GoldenMagma
import com.mikepenz.hypnoticcanvas.shaders.GradientFlow
import com.mikepenz.hypnoticcanvas.shaders.IceReflection
import com.mikepenz.hypnoticcanvas.shaders.InkFlow
import com.mikepenz.hypnoticcanvas.shaders.MeshGradient
import com.mikepenz.hypnoticcanvas.shaders.MesmerizingLens
import com.mikepenz.hypnoticcanvas.shaders.OilFlow
import com.mikepenz.hypnoticcanvas.shaders.PurpleLiquid
import com.mikepenz.hypnoticcanvas.shaders.Shader
import com.mikepenz.hypnoticcanvas.shaders.Stage
import it.fast4x.rimusic.Database
import it.fast4x.rimusic.LocalPlayerServiceBinder
import it.fast4x.rimusic.appRunningInBackground
import it.fast4x.rimusic.cleanPrefix
import it.fast4x.rimusic.colorPalette
import it.fast4x.rimusic.enums.AnimatedGradient
import it.fast4x.rimusic.enums.BackgroundProgress
import it.fast4x.rimusic.enums.ColorPaletteMode
import it.fast4x.rimusic.enums.NavRoutes
import it.fast4x.rimusic.enums.PlayerBackgroundColors
import it.fast4x.rimusic.enums.PlayerType
import it.fast4x.rimusic.enums.QueueType
import it.fast4x.rimusic.enums.SwipeAnimationNoThumbnail
import it.fast4x.rimusic.enums.ThumbnailType
import it.fast4x.rimusic.models.Info
import it.fast4x.rimusic.models.ui.toUiMedia
import it.fast4x.rimusic.thumbnailShape
import it.fast4x.rimusic.typography
import it.fast4x.rimusic.ui.components.CustomModalBottomSheet
import it.fast4x.rimusic.ui.components.LocalMenuState
import it.fast4x.rimusic.ui.components.themed.CircularSlider
import it.fast4x.rimusic.ui.components.themed.ConfirmationDialog
import it.fast4x.rimusic.ui.components.themed.DefaultDialog
import it.fast4x.rimusic.ui.components.themed.IconButton
import it.fast4x.rimusic.ui.components.themed.NowPlayingSongIndicator
import it.fast4x.rimusic.ui.components.themed.PlayerMenu
import it.fast4x.rimusic.ui.components.themed.RotateThumbnailCoverAnimationModern
import it.fast4x.rimusic.ui.components.themed.SecondaryTextButton
import it.fast4x.rimusic.ui.components.themed.ThumbnailOffsetDialog
import it.fast4x.rimusic.ui.components.themed.animateBrushRotation
import it.fast4x.rimusic.ui.styling.Dimensions
import it.fast4x.rimusic.ui.styling.collapsedPlayerProgressBar
import it.fast4x.rimusic.ui.styling.dynamicColorPaletteOf
import it.fast4x.rimusic.ui.styling.favoritesOverlay
import it.fast4x.rimusic.utils.DisposableListener
import it.fast4x.rimusic.utils.SearchYoutubeEntity
import it.fast4x.rimusic.utils.VerticalfadingEdge2
import it.fast4x.rimusic.utils.currentWindow
import it.fast4x.rimusic.utils.doubleShadowDrop
import it.fast4x.rimusic.utils.durationTextToMillis
import it.fast4x.rimusic.utils.formatAsDuration
import it.fast4x.rimusic.utils.formatAsTime
import it.fast4x.rimusic.utils.getBitmapFromUrl
import it.fast4x.rimusic.utils.horizontalFadingEdge
import it.fast4x.rimusic.utils.isExplicit
import it.fast4x.rimusic.utils.isLandscape
import it.fast4x.rimusic.utils.mediaItems
import it.fast4x.rimusic.utils.playAtIndex
import it.fast4x.rimusic.utils.playNext
import it.fast4x.rimusic.utils.playPrevious
import it.fast4x.rimusic.utils.positionAndDurationState
import it.fast4x.rimusic.utils.semiBold
import it.fast4x.rimusic.utils.shouldBePlaying
import it.fast4x.rimusic.utils.thumbnail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import me.knighthat.coil.ImageCacheFactory
import me.knighthat.component.player.BlurAdjuster
import me.knighthat.utils.Toaster
import kotlin.Float.Companion.POSITIVE_INFINITY
import kotlin.math.absoluteValue
import kotlin.math.sqrt


@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalTextApi
@SuppressLint("SuspiciousIndentation", "RememberReturnType", "NewApi")
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@UnstableApi
@Composable
fun Player(
    navController: NavController,
    onDismiss: () -> Unit,
) {
    // Essentails
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val menuState = LocalMenuState.current
    val binder = LocalPlayerServiceBinder.current ?: return
    // Settings
    val disablePlayerHorizontalSwipe by Preferences.PLAYER_THUMBNAIL_HORIZONTAL_SWIPE_DISABLED
    val showlyricsthumbnail by Preferences.LYRICS_SHOW_THUMBNAIL
    val effectRotationEnabled by Preferences.ROTATION_EFFECT
    val playerThumbnailSize by Preferences.PLAYER_PORTRAIT_THUMBNAIL_SIZE
    var playerThumbnailSizeL by Preferences.PLAYER_LANDSCAPE_THUMBNAIL_SIZE
    val showvisthumbnail by Preferences.PLAYER_SHOW_THUMBNAIL_ON_VISUALIZER
    var thumbnailSpacing  by Preferences.PLAYER_THUMBNAIL_SPACING
    var thumbnailSpacingL  by Preferences.PLAYER_THUMBNAIL_SPACING_LANDSCAPE
    var thumbnailFade  by Preferences.PLAYER_THUMBNAIL_FADE
    var thumbnailFadeEx  by Preferences.PLAYER_THUMBNAIL_FADE_EX
    var imageCoverSize by Preferences.PLAYER_THUMBNAIL_VINYL_SIZE
    val queueDurationExpanded by Preferences.PLAYER_IS_QUEUE_DURATION_EXPANDED
    val statsExpanded by Preferences.PLAYER_IS_STATS_FOR_NERDS_EXPANDED
    var showthumbnail by Preferences.PLAYER_SHOW_THUMBNAIL
    val showButtonPlayerMenu by Preferences.PLAYER_ACTION_SHOW_MENU
    val showTotalTimeQueue by Preferences.PLAYER_SHOW_TOTAL_QUEUE_TIME
    val backgroundProgress by Preferences.MINI_PLAYER_PROGRESS_BAR
    var queueLoopState = Preferences.QUEUE_LOOP_TYPE
    val playerType by Preferences.PLAYER_TYPE
    val queueType by Preferences.QUEUE_TYPE
    val noblur by Preferences.PLAYER_BACKGROUND_BLUR
    val fadingedge by Preferences.PLAYER_BACKGROUND_FADING_EDGE
    val colorPaletteMode by Preferences.THEME_MODE
    val playerBackgroundColors by Preferences.PLAYER_BACKGROUND
    val animatedGradient by Preferences.ANIMATED_GRADIENT
    val thumbnailTapEnabled by Preferences.PLAYER_TAP_THUMBNAIL_FOR_LYRICS
    val showTopActionsBar by Preferences.PLAYER_SHOW_TOP_ACTIONS_BAR
    val blackgradient by Preferences.BLACK_GRADIENT
    val bottomgradient by Preferences.PLAYER_BOTTOM_GRADIENT
    val disableScrollingText by Preferences.SCROLLING_TEXT_DISABLED
    var discoverState = Preferences.ENABLE_DISCOVER
    val titleExpanded by Preferences.PLAYER_IS_TITLE_EXPANDED
    val timelineExpanded by Preferences.PLAYER_IS_TIMELINE_EXPANDED
    val controlsExpanded by Preferences.PLAYER_IS_CONTROLS_EXPANDED
    val showCoverThumbnailAnimation by Preferences.PLAYER_THUMBNAIL_ANIMATION
    var coverThumbnailAnimation by Preferences.PLAYER_THUMBNAIL_TYPE
    var albumCoverRotation by Preferences.PLAYER_THUMBNAIL_ROTATION
    val textoutline by Preferences.TEXT_OUTLINE
    val carousel by Preferences.PLAYER_THUMBNAILS_CAROUSEL
    val carouselSize by Preferences.CAROUSEL_SIZE
    val clickLyricsText by Preferences.LYRICS_JUMP_ON_TAP
    var extraspace by Preferences.PLAYER_EXTRA_SPACE
    val thumbnailRoundness by Preferences.THUMBNAIL_BORDER_RADIUS
    val thumbnailType by Preferences.THUMBNAIL_TYPE
    val statsfornerds by Preferences.PLAYER_STATS_FOR_NERDS
    val topPadding by Preferences.PLAYER_TOP_PADDING
    var swipeAnimationNoThumbnail by Preferences.PLAYER_NO_THUMBNAIL_SWIPE_ANIMATION
    val expandPlayerState = Preferences.PLAYER_EXPANDED
    var expandedplayer by expandPlayerState


    if (binder.player.currentTimeline.windowCount == 0) return

    var nullableMediaItem by remember {
        mutableStateOf(binder.player.currentMediaItem, neverEqualPolicy())
    }

    var shouldBePlaying by remember {
        mutableStateOf(binder.player.shouldBePlaying)
    }

    val rotateState = rememberSaveable { mutableStateOf( false ) }
    var isRotated by rotateState
    val rotationAngle by animateFloatAsState(
        targetValue = if (isRotated) 360F else 0f,
        animationSpec = tween(durationMillis = 200), label = ""
    )

    val showQueueState = rememberSaveable { mutableStateOf( false ) }
    var showQueue by showQueueState

    val showSearchEntityState = rememberSaveable { mutableStateOf( false ) }
    var showSearchEntity by showSearchEntityState

    val showVisualizerState = rememberSaveable { mutableStateOf( false ) }
    var isShowingVisualizer by showVisualizerState

    val showSleepTimerState = rememberSaveable { mutableStateOf( false ) }
    var isShowingSleepTimerDialog by showSleepTimerState

    val showLyricsState = rememberSaveable { mutableStateOf( false ) }
    var isShowingLyrics by showLyricsState

    var showThumbnailOffsetDialog by rememberSaveable {
        mutableStateOf(false)
    }

    if (showThumbnailOffsetDialog) {

        ThumbnailOffsetDialog(
            onDismiss = { showThumbnailOffsetDialog = false},
            spacingValue = { thumbnailSpacing = it },
            spacingValueL = { thumbnailSpacingL = it },
            fadeValue = { thumbnailFade = it },
            fadeValueEx = { thumbnailFadeEx = it },
            imageCoverSizeValue = { imageCoverSize = it }
        )
    }

    var mediaItems by remember {
        mutableStateOf(binder.player.currentTimeline.mediaItems)
    }
    var playerError by remember {
        mutableStateOf<PlaybackException?>(binder.player.playerError)
    }

    fun PagerState.offsetForPage(page: Int) = (currentPage - page) + currentPageOffsetFraction

    fun PagerState.startOffsetForPage(page: Int): Float {
        return offsetForPage(page).coerceAtLeast(0f)
    }

    fun PagerState.endOffsetForPage(page: Int): Float {
        return offsetForPage(page).coerceAtMost(0f)
    }

    class CirclePath(private val progress: Float, private val origin: Offset = Offset(0f, 0f)) : Shape {
        override fun createOutline(
            size: Size, layoutDirection: LayoutDirection, density: Density
        ): Outline {

            val center = Offset(
                x = size.center.x - ((size.center.x - origin.x) * (1f - progress)),
                y = size.center.y - ((size.center.y - origin.y) * (1f - progress)),
            )
            val radius = (sqrt(
                size.height * size.height + size.width * size.width
            ) * .5f) * progress

            return Outline.Generic(Path().apply {
                addOval(
                    Rect(
                        center = center,
                        radius = radius,
                    )
                )
            })
        }
    }

    binder.player.DisposableListener {
        object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                nullableMediaItem = mediaItem
            }

            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                shouldBePlaying = playerError == null && binder.player.shouldBePlaying
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                playerError = binder.player.playerError
                shouldBePlaying = playerError == null && binder.player.shouldBePlaying
            }
            override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                mediaItems = timeline.mediaItems
            }
            override fun onPlayerError(playbackException: PlaybackException) {
                playerError = playbackException
            }
        }
    }

    val
            mediaItem = nullableMediaItem ?: return

    val pagerState = rememberPagerState(pageCount = { mediaItems.size })
    val pagerStateFS = rememberPagerState(pageCount = { mediaItems.size })
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()
    val isDraggedFS by pagerStateFS.interactionSource.collectIsDraggedAsState()

    var delayedSleepTimer by remember {
        mutableStateOf(false)
    }

    val sleepTimerMillisLeft by (binder.sleepTimerMillisLeft
        ?: flowOf(null))
        .collectAsState(initial = null)

    val positionAndDuration by binder.player.positionAndDurationState()
    var timeRemaining by remember { mutableIntStateOf(0) }
    timeRemaining = positionAndDuration.second.toInt() - positionAndDuration.first.toInt()

    if (sleepTimerMillisLeft != null)
        if (sleepTimerMillisLeft!! < timeRemaining.toLong() && !delayedSleepTimer)  {
            binder.cancelSleepTimer()
            binder.startSleepTimer(timeRemaining.toLong())
            delayedSleepTimer = true
            Toaster.n( R.string.info_sleep_timer_delayed_at_end_of_song )
        }

    val windowInsets = WindowInsets.systemBars

    var updateBrush by remember { mutableStateOf(false) }

    if (showlyricsthumbnail) expandedplayer = false

    LaunchedEffect(mediaItem.mediaId) {
        updateBrush = true
    }

    val artistInfos by remember( mediaItem ) {
        val ids = mediaItem.mediaMetadata.extras?.getStringArrayList( "artistIds" ).orEmpty()
        val names = mediaItem.mediaMetadata.extras?.getStringArrayList( "artistNames" ).orEmpty()

        if( ids.isNotEmpty() )
            return@remember flowOf (
                ids.fastZip( names ) { id, name -> Info(id, name) }
            )

        Database.songArtistMapTable
                .findArtistsOf( mediaItem.mediaId )
                .distinctUntilChanged()
                .map { list ->
                    list.map { Info(it.id, it.name) }
                }
    }.collectAsState( emptyList(), Dispatchers.IO )
    val albumId by remember( mediaItem ) {
        val result = mediaItem.mediaMetadata.extras?.getString("albumId")
        if( !result.isNullOrBlank() )
            return@remember flowOf( result )

        Database.songAlbumMapTable
                .findAlbumOf( mediaItem.mediaId )
                .map { it?.id }
    }.collectAsState( null, Dispatchers.IO )

    var showCircularSlider by remember {
        mutableStateOf(false)
    }
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    if (isShowingSleepTimerDialog) {
        if (sleepTimerMillisLeft != null) {
            ConfirmationDialog(
                text = stringResource(R.string.stop_sleep_timer),
                cancelText = stringResource(R.string.no),
                confirmText = stringResource(R.string.stop),
                onDismiss = { isShowingSleepTimerDialog = false },
                onConfirm = {
                    binder.cancelSleepTimer()
                    delayedSleepTimer = false
                    //onDismiss()
                }
            )
        } else {
            DefaultDialog(
                onDismiss = { isShowingSleepTimerDialog = false }
            ) {
                var amount by remember {
                    mutableStateOf(1)
                }

                BasicText(
                    text = stringResource(R.string.set_sleep_timer),
                    style = typography().s.semiBold,
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 24.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 16.dp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                ) {
                    if (!showCircularSlider) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .alpha(if (amount <= 1) 0.5f else 1f)
                                .clip(CircleShape)
                                .clickable(enabled = amount > 1) { amount-- }
                                .size(48.dp)
                                .background(colorPalette().background0)
                        ) {
                            BasicText(
                                text = "-",
                                style = typography().xs.semiBold
                            )
                        }

                        Box(contentAlignment = Alignment.Center) {
                            BasicText(
                                text = stringResource(
                                    R.string.left,
                                    formatAsDuration(amount * 5 * 60 * 1000L)
                                ),
                                style = typography().s.semiBold,
                                modifier = Modifier
                                    .clickable {
                                        showCircularSlider = !showCircularSlider
                                    }
                            )
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .alpha(if (amount >= 60) 0.5f else 1f)
                                .clip(CircleShape)
                                .clickable(enabled = amount < 60) { amount++ }
                                .size(48.dp)
                                .background(colorPalette().background0)
                        ) {
                            BasicText(
                                text = "+",
                                style = typography().xs.semiBold
                            )
                        }

                    } else {
                        CircularSlider(
                            stroke = 40f,
                            thumbColor = colorPalette().accent,
                            text = formatAsDuration(amount * 5 * 60 * 1000L),
                            modifier = Modifier
                                .size(300.dp),
                            onChange = {
                                amount = (it * 120).toInt()
                            }
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .fillMaxWidth()
                ) {
                    SecondaryTextButton(
                        text = stringResource(R.string.set_to) + " "
                                + formatAsDuration(timeRemaining.toLong())
                                + " " + stringResource(R.string.end_of_song),
                        onClick = {
                            binder.startSleepTimer(timeRemaining.toLong())
                            isShowingSleepTimerDialog = false
                        }
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {

                    IconButton(
                        onClick = { showCircularSlider = !showCircularSlider },
                        icon = R.drawable.time,
                        color = colorPalette().text
                    )
                    IconButton(
                        onClick = { isShowingSleepTimerDialog = false },
                        icon = R.drawable.close,
                        color = colorPalette().text
                    )
                    IconButton(
                        enabled = amount > 0,
                        onClick = {
                            binder.startSleepTimer(amount * 5 * 60 * 1000L)
                            isShowingSleepTimerDialog = false
                        },
                        icon = R.drawable.checkmark,
                        color = colorPalette().accent
                    )
                }
            }
        }
    }

    val color = colorPalette()
    var dynamicColorPalette by remember { mutableStateOf( color ) }
    var dominant by remember{ mutableStateOf(0) }
    var vibrant by remember{ mutableStateOf(0) }
    var lightVibrant by remember{ mutableStateOf(0) }
    var darkVibrant by remember{ mutableStateOf(0) }
    var muted by remember{ mutableStateOf(0) }
    var lightMuted by remember{ mutableStateOf(0) }
    var darkMuted by remember{ mutableStateOf(0) }


    @Composable
    fun saturate(color : Int): Color {
        val colorHSL by remember { mutableStateOf(floatArrayOf(0f, 0f, 0f)) }
        val lightTheme = colorPaletteMode == ColorPaletteMode.Light || (colorPaletteMode == ColorPaletteMode.System && (!isSystemInDarkTheme()))
        colorToHSL(color,colorHSL)
        colorHSL[1] = (colorHSL[1] + if (lightTheme || colorHSL[1] < 0.1f) 0f else 0.35f).coerceIn(0f,1f)
        colorHSL[2] = if (lightTheme) {colorHSL[2].coerceIn(0.5f,1f)} else colorHSL[2]
        return Color.hsl(colorHSL[0],colorHSL[1],colorHSL[2])
    }

    var lightTheme = colorPaletteMode == ColorPaletteMode.Light || (colorPaletteMode == ColorPaletteMode.System && (!isSystemInDarkTheme()))
    var ratio = if (lightTheme) 1f else 0.5f

    fun Color.darkenBy(): Color {
        return copy(
            red = red * ratio,
            green = green * ratio,
            blue = blue * ratio,
            alpha = alpha
        )
    }


    val isGradientBackgroundEnabled =
        playerBackgroundColors == PlayerBackgroundColors.ThemeColorGradient ||
                playerBackgroundColors == PlayerBackgroundColors.CoverColorGradient ||
                playerBackgroundColors == PlayerBackgroundColors.AnimatedGradient

        println("Player url mediaitem ${mediaItem.mediaMetadata.artworkUri}")
        println("Player url binder ${binder.player.currentWindow?.mediaItem?.mediaMetadata?.artworkUri}")
    LaunchedEffect(mediaItem.mediaId, updateBrush) {
        if (playerBackgroundColors == PlayerBackgroundColors.CoverColorGradient ||
            playerBackgroundColors == PlayerBackgroundColors.CoverColor ||
            playerBackgroundColors == PlayerBackgroundColors.AnimatedGradient || updateBrush
        ) {
            try {
                val bitmap = getBitmapFromUrl(
                    context,
                    binder.player.currentWindow?.mediaItem?.mediaMetadata?.artworkUri.thumbnail(1200).toString()
                )

                dynamicColorPalette = dynamicColorPaletteOf(
                    bitmap,
                    !lightTheme
                ) ?: color
                println("Player INSIDE getting dynamic color $dynamicColorPalette")

                val palette = Palette.from(bitmap).generate()

                dominant = palette.getDominantColor(dynamicColorPalette.accent.toArgb())
                vibrant = palette.getVibrantColor(dynamicColorPalette.accent.toArgb())
                lightVibrant = palette.getLightVibrantColor(dynamicColorPalette.accent.toArgb())
                darkVibrant = palette.getDarkVibrantColor(dynamicColorPalette.accent.toArgb())
                muted = palette.getMutedColor(dynamicColorPalette.accent.toArgb())
                lightMuted = palette.getLightMutedColor(dynamicColorPalette.accent.toArgb())
                darkMuted = palette.getDarkMutedColor(dynamicColorPalette.accent.toArgb())

            } catch (e: Exception) {
                dynamicColorPalette = color
                println("Player Error getting dynamic color ${e.printStackTrace()}")
            }
        }
        println("Player after getting dynamic color $dynamicColorPalette")
    }

    var sizeShader by remember { mutableStateOf(Size.Zero) }

    var totalPlayTimes = 0L
    mediaItems.forEach {
        totalPlayTimes += it.mediaMetadata.extras?.getString("durationText")?.let { it1 ->
            durationTextToMillis(it1)
        }?.toLong() ?: 0
    }


    var isShowingStatsForNerds by rememberSaveable {
        mutableStateOf(false)
    }


    var containerModifier = Modifier
        .padding(bottom = 0.dp)
    var deltaX by remember { mutableStateOf(0f) }


    var valueGrad by remember{ mutableStateOf(2) }
    val gradients = enumValues<AnimatedGradient>()
    var tempGradient by remember{ mutableStateOf(AnimatedGradient.Linear) }
    var circleOffsetY by remember {mutableStateOf(0f)}

    @Composable
    fun Modifier.conditional(condition : Boolean, modifier : @Composable Modifier.() -> Modifier) : Modifier {
        return if (condition) {
            then(modifier(Modifier))
        } else {
            this
        }
    }

    if (animatedGradient == AnimatedGradient.Random){
        LaunchedEffect(mediaItem.mediaId){
            valueGrad = (2..13).random()
        }
        tempGradient = gradients[valueGrad]
    }

    val blurAdjuster = BlurAdjuster()

    if (!isGradientBackgroundEnabled) {
        if (playerBackgroundColors == PlayerBackgroundColors.BlurredCoverColor && (playerType == PlayerType.Essential || (showthumbnail && (!albumCoverRotation)))) {
            containerModifier = containerModifier
                .background(
                    Brush.verticalGradient(
                        0.0f to Color.Transparent,
                        1.0f to if (bottomgradient) if (colorPaletteMode == ColorPaletteMode.Light) Color.White.copy(
                            if (isLandscape) 0.8f else 0.75f
                        ) else Color.Black.copy(if (isLandscape) 0.8f else 0.75f) else Color.Transparent,
                        startY = if (isLandscape) 600f else if (expandedplayer) 1300f else 950f,
                        endY = POSITIVE_INFINITY
                    )
                )
                .background(
                    if (bottomgradient) if (isLandscape) if (colorPaletteMode == ColorPaletteMode.Light) Color.White.copy(
                        0.25f
                    ) else Color.Black.copy(0.25f) else Color.Transparent else Color.Transparent
                )
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        if (thumbnailTapEnabled && !showthumbnail) {
                            if (isShowingVisualizer) isShowingVisualizer = false
                            isShowingLyrics = !isShowingLyrics
                        }
                    },
                    onDoubleClick = {
                        if (!showlyricsthumbnail && !showvisthumbnail)
                            showthumbnail = !showthumbnail
                    },
                    onLongClick = {
                        blurAdjuster.isActive =
                            showthumbnail || (isShowingLyrics && !isShowingVisualizer) || !noblur
                    }
                )
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, dragAmount ->
                            deltaX = dragAmount
                        },
                        onDragStart = {
                        },
                        onDragEnd = {
                            if (!disablePlayerHorizontalSwipe && playerType == PlayerType.Essential) {
                                if (deltaX > 5) {
                                    binder.player.playPrevious()
                                } else if (deltaX < -5) {
                                    binder.player.playNext()
                                }

                            }

                        }

                    )
                }

        } else if (playerBackgroundColors == PlayerBackgroundColors.ColorPalette){
            containerModifier = containerModifier
                .drawBehind {
                    val colors = listOf(Color(dominant),Color(vibrant),Color(lightVibrant),Color(darkVibrant),Color(muted),Color(lightMuted),Color(darkMuted))
                    val boxheight = (size.height)/7
                    colors.forEachIndexed {i, _ ->
                        drawRect(
                            color = colors[i],
                            topLeft = Offset(0f,i*boxheight),
                            size = Size(size.width,boxheight)
                        )
                    }
                }
        } else if (playerBackgroundColors == PlayerBackgroundColors.CoverColor){
            containerModifier = containerModifier
                .background(dynamicColorPalette.background1)
        } else if (playerBackgroundColors == PlayerBackgroundColors.ThemeColor){
            containerModifier = containerModifier
                .background(color.background1)
        }
    } else {
        when (playerBackgroundColors) {
            PlayerBackgroundColors.AnimatedGradient -> {
                var background by remember {
                    mutableStateOf( Color.Transparent )
                }
                var shaderCondition by remember {
                    mutableStateOf( true )
                }
                var shader: Shader? by remember {
                    mutableStateOf( null )
                }
                val type = remember( animatedGradient, tempGradient ) {
                    if( animatedGradient == AnimatedGradient.Random )
                        tempGradient
                    else
                        animatedGradient
                }

                when( type ) {
                    AnimatedGradient.FluidThemeColorGradient, AnimatedGradient.FluidCoverColorGradient -> {
                        val shaderA = LinearGradientShader(
                            Offset(sizeShader.width / 2f, 0f),
                            Offset(sizeShader.width / 2f, sizeShader.height),
                            listOf(
                                dynamicColorPalette.background2,
                                colorPalette().background2,
                            ),
                            listOf(0f, 1f)
                        )
                        val brushA by animateBrushRotation(shaderA, sizeShader, 20_000, true)

                        val shaderB = LinearGradientShader(
                            Offset(sizeShader.width / 2f, 0f),
                            Offset(sizeShader.width / 2f, sizeShader.height),
                            listOf(
                                colorPalette().background1,
                                dynamicColorPalette.accent,
                            ),
                            listOf(0f, 1f)
                        )
                        val brushB by animateBrushRotation(shaderB, sizeShader, 12_000, false)

                        val shaderMask = LinearGradientShader(
                            Offset(sizeShader.width / 2f, 0f),
                            Offset(sizeShader.width / 2f, sizeShader.height),
                            listOf(
                                //Color.White,
                                colorPalette().background2,
                                Color.Transparent,
                            ),
                            listOf(0f, 1f)
                        )
                        val brushMask by animateBrushRotation(shaderMask, sizeShader, 15_000, true)

                        containerModifier = containerModifier
                            .drawBehind {
                                drawRect(brush = brushA)
                                drawRect(brush = brushMask, blendMode = BlendMode.DstOut)
                                drawRect(brush = brushB, blendMode = BlendMode.DstAtop)
                            }
                    }
                    AnimatedGradient.Linear -> {
                        containerModifier = containerModifier.animatedGradient(
                            binder.player.isPlaying,
                            saturate(dominant).darkenBy(),
                            saturate(vibrant).darkenBy(),
                            saturate(lightVibrant).darkenBy(),
                            saturate(darkVibrant).darkenBy(),
                            saturate(muted).darkenBy(),
                            saturate(lightMuted).darkenBy(),
                            saturate(darkMuted).darkenBy()
                        )
                    }
                    AnimatedGradient.Mesh -> {
                        shaderCondition = !appRunningInBackground
                        shader = MeshGradient(
                            colors = arrayOf(
                                saturate(vibrant).darkenBy(),
                                saturate(lightVibrant).darkenBy(),
                                saturate(darkVibrant).darkenBy(),
                                saturate(muted).darkenBy(),
                                saturate(lightMuted).darkenBy(),
                                saturate(darkMuted).darkenBy(),
                                saturate(dominant).darkenBy()
                            ),
                            scale = 1f
                        )
                    }
                    AnimatedGradient.MesmerizingLens -> shader = MesmerizingLens
                    AnimatedGradient.GlossyGradients -> {
                        if( !lightTheme )
                            background = Color.Black.copy(.2f)
                        shader = GlossyGradients
                    }
                    AnimatedGradient.GradientFlow -> {
                        if( !lightTheme )
                            background = Color.Black.copy(.2f)
                        shader = GradientFlow
                    }
                    AnimatedGradient.PurpleLiquid -> shader = PurpleLiquid
                    AnimatedGradient.InkFlow -> {
                        if( lightTheme )
                            background = Color.White.copy(.4f)
                        shader = InkFlow
                    }
                    AnimatedGradient.OilFlow -> {
                        if( lightTheme )
                            background = Color.White.copy(.4f)
                        shader = OilFlow
                    }
                    AnimatedGradient.IceReflection -> {
                        background = if( !lightTheme ) Color.Black.copy( .3f ) else Color.White.copy( .4f )
                        shader = IceReflection
                    }
                    AnimatedGradient.Stage -> {
                        if( !lightTheme )
                            background = Color.Black.copy( .3f )
                        shader = Stage
                    }
                    AnimatedGradient.GoldenMagma -> {
                        background = if( !lightTheme ) Color.Black.copy( .2f ) else Color.White.copy( .3f )
                        shader = GoldenMagma
                    }
                    AnimatedGradient.BlackCherryCosmos -> {
                        if( lightTheme )
                            background = Color.White.copy( .35f )
                        shader = BlackCherryCosmos
                    }
                    // if [animatedGradient] is [AnimatedGradient.Random] then it should choose
                    // [tempGradient]. If [tempGradient] is [AnimatedGradient.Random], you have
                    // a problem.
                    AnimatedGradient.Random -> throw IllegalStateException("Anything but this")
                }

                containerModifier = containerModifier.conditional( shaderCondition && shader != null ) {
                                                         shaderBackground( shader!! )
                                                     }
                                                     .background( background )
                                                     .onSizeChanged {
                                                         sizeShader = Size( it.width.toFloat(), it.height.toFloat() )
                                                     }
            }

            else -> {
                containerModifier = containerModifier
                    .background(
                        Brush.verticalGradient(
                            0.5f to if (playerBackgroundColors == PlayerBackgroundColors.CoverColorGradient) dynamicColorPalette.background1 else colorPalette().background1,
                            1.0f to if (blackgradient) Color.Black else colorPalette().background2,
                            startY = 0.0f,
                            endY = 1500.0f
                        )
                    )

            }
        }

    }

    val thumbnailContent: @Composable () -> Unit = {
        var deltaX by remember { mutableStateOf(0f) }

        val isSongLiked by remember( mediaItem.mediaId ) {
            Database.songTable
                .isLiked( mediaItem.mediaId )
                .distinctUntilChanged()
        }.collectAsState( false, Dispatchers.IO )

            Thumbnail(
                thumbnailTapEnabledKey = thumbnailTapEnabled,
                isShowingLyrics = isShowingLyrics,
                onShowLyrics = { isShowingLyrics = it },
                isShowingStatsForNerds = isShowingStatsForNerds,
                onShowStatsForNerds = { isShowingStatsForNerds = it },
                isShowingVisualizer = isShowingVisualizer,
                onShowEqualizer = { isShowingVisualizer = it },
                showthumbnail = showthumbnail,
                onMaximize = {},
                onDoubleTap = {
                    val currentMediaItem = binder.player.currentMediaItem
                    Database.asyncTransaction {
                        if( !isSongLiked )
                            currentMediaItem
                                ?.takeIf { it.mediaId == mediaItem.mediaId }
                                ?.let {
                                    insertIgnore( currentMediaItem )
                                    songTable.toggleLike( currentMediaItem.mediaId )
                                }
                    }
                    if (effectRotationEnabled) isRotated = !isRotated
                },
                modifier = Modifier
                    //.nestedScroll( connection = scrollConnection )
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onHorizontalDrag = { _, dragAmount ->
                                deltaX = dragAmount
                            },
                            onDragStart = {
                            },
                            onDragEnd = {
                                if (!disablePlayerHorizontalSwipe && playerType == PlayerType.Essential) {
                                    if (deltaX > 5) {
                                        binder.player.playPrevious()
                                    } else if (deltaX < -5) {
                                        binder.player.playNext()
                                    }

                                }

                            }

                        )
                    }
                    .padding(all = if (isLandscape) playerThumbnailSizeL.size.dp else playerThumbnailSize.size.dp)
                    .thumbnailpause(
                        shouldBePlaying = shouldBePlaying
                    )

            )

    }

    @Composable
    fun Controller( mediaItem: MediaItem, modifier: Modifier ) {
        Controls(
            navController = navController,
            onCollapse = onDismiss,
            onBlurScaleChange = { blurAdjuster.strength = it },
            expandPlayer = expandedplayer,
            titleExpanded = titleExpanded,
            timelineExpanded = timelineExpanded,
            controlsExpanded = controlsExpanded,
            isShowingLyrics = isShowingLyrics,
            mediaItem = mediaItem,
            artistIds = artistInfos,
            albumId = albumId,
            shouldBePlaying = shouldBePlaying,
            positionAndDuration = positionAndDuration,
            modifier = modifier,
        )
    }

    blurAdjuster.Render()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        @Composable
        fun ActionsBar() = ActionBar(
            navController,
            showQueueState,
            showSearchEntityState,
            rotateState,
            showVisualizerState,
            showSleepTimerState,
            showLyricsState,
            discoverState,
            queueLoopState,
            expandPlayerState,
            onDismiss
        )

        val player = binder.player

        if (isLandscape) {
         Box{
             if (playerBackgroundColors == PlayerBackgroundColors.BlurredCoverColor && playerType == PlayerType.Modern && (!showthumbnail || albumCoverRotation)) {
                 val fling = PagerDefaults.flingBehavior(
                     state = pagerStateFS,
                     snapPositionalThreshold = 0.20f
                 )
                 pagerStateFS.LaunchedEffectScrollToPage(binder.player.currentMediaItemIndex)

                 LaunchedEffect(pagerStateFS) {
                     var previousPage = pagerStateFS.settledPage
                     snapshotFlow { pagerStateFS.settledPage }.distinctUntilChanged().collect {
                         if (previousPage != it) {
                             if (it != binder.player.currentMediaItemIndex) binder.player.playAtIndex(it)
                         }
                         previousPage = it
                     }
                 }

                 HorizontalPager(
                     state = pagerStateFS,
                     beyondViewportPageCount = 1,
                     flingBehavior = fling,
                     userScrollEnabled = !(albumCoverRotation && (isShowingLyrics || showthumbnail)),
                     modifier = Modifier
                 ) {

                     var currentRotation by remember {
                         mutableFloatStateOf(0f)
                     }

                     val rotation = remember {
                         Animatable(currentRotation)
                     }

                     LaunchedEffect(player.isPlaying, pagerStateFS.settledPage) {
                         if (player.isPlaying && it == pagerStateFS.settledPage) {
                             rotation.animateTo(
                                 targetValue = currentRotation + 360f,
                                 animationSpec = infiniteRepeatable(
                                     animation = tween(30000, easing = LinearEasing),
                                     repeatMode = RepeatMode.Restart
                                 )
                             ) {
                                 currentRotation = value
                             }
                         } else {
                             if (currentRotation > 0f && it == pagerStateFS.settledPage) {
                                 rotation.animateTo(
                                     targetValue = currentRotation + 10,
                                     animationSpec = tween(
                                         1250,
                                         easing = LinearOutSlowInEasing
                                     )
                                 ) {
                                     currentRotation = value
                                 }
                             }
                         }
                     }

                     BlurredCover(
                         thumbnailUrl = binder.player.getMediaItemAt(it).mediaMetadata.artworkUri.toString(),
                         blurAdjuster = blurAdjuster,
                         showThumbnail = showthumbnail,
                         noBlur = noblur,
                         isShowingLyrics = isShowingLyrics,
                         isShowingVisualizer = isShowingVisualizer,
                         contentScale = if (albumCoverRotation && (isShowingLyrics || showthumbnail)) ContentScale.Fit else ContentScale.Crop,
                         modifier = Modifier.zIndex(if (it == pagerStateFS.currentPage) 1f else 0.9f)
                                            .conditional(albumCoverRotation) {
                                                graphicsLayer {
                                                    scaleX = if (isShowingLyrics || showthumbnail) (screenWidth/screenHeight) + 0.5f else 1f
                                                    scaleY = if (isShowingLyrics || showthumbnail) (screenWidth/screenHeight) + 0.5f else 1f
                                                    rotationZ = if ((it == pagerStateFS.settledPage) && (isShowingLyrics || showthumbnail)) rotation.value else 0f
                                                }
                                            }
                                            .combinedClickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = null,
                                                onClick = {
                                                    if (thumbnailTapEnabled && !showthumbnail) {
                                                        if (isShowingVisualizer) isShowingVisualizer = false
                                                        isShowingLyrics = !isShowingLyrics
                                                    }
                                                },
                                                onDoubleClick = {
                                                    if (!showlyricsthumbnail && !showvisthumbnail)
                                                        showthumbnail = !showthumbnail
                                                },
                                                onLongClick = {
                                                    blurAdjuster.isActive = showthumbnail || (isShowingLyrics && !isShowingVisualizer) || !noblur
                                                }
                                            )
                     )
                 }

                 Column(modifier = Modifier
                     .matchParentSize()
                     .background(
                         Brush.verticalGradient(
                             0.0f to Color.Transparent,
                             1.0f to if (bottomgradient) if (lightTheme) Color.White.copy(
                                 if (isLandscape) 0.8f else 0.75f
                             ) else Color.Black.copy(if (isLandscape) 0.8f else 0.75f) else Color.Transparent,
                             startY = if (isLandscape) 600f else if (expandedplayer) 1300f else 950f,
                             endY = POSITIVE_INFINITY
                         )
                     )
                     .background(
                         if (bottomgradient) if (isLandscape) if (lightTheme) Color.White.copy(
                             0.25f
                         ) else Color.Black.copy(0.25f) else Color.Transparent else Color.Transparent
                     )){}
             }

             BlurredCover(
                 thumbnailUrl = binder.player.mediaMetadata.artworkUri.toString(),
                 blurAdjuster = blurAdjuster,
                 showThumbnail = showthumbnail,
                 noBlur = noblur,
                 isShowingLyrics = isShowingLyrics,
                 isShowingVisualizer = isShowingVisualizer,
                 contentScale = ContentScale.FillHeight
             )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = containerModifier
                    .padding(top = if (playerType == PlayerType.Essential) 40.dp else 20.dp)
                    .padding(top = if (extraspace) 10.dp else 0.dp)
                    .drawBehind {
                        if (backgroundProgress == BackgroundProgress.Both || backgroundProgress == BackgroundProgress.Player) {
                            drawRect(
                                color = color.favoritesOverlay,
                                topLeft = Offset.Zero,
                                size = Size(
                                    width = positionAndDuration.first.toFloat() /
                                            positionAndDuration.second.absoluteValue * size.width,
                                    height = size.maxDimension
                                )
                            )
                        }
                    }
            ) {
                Column (
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxHeight()
                        .animateContentSize()
                       // .border(BorderStroke(1.dp, Color.Blue))
                ) {
                    if (showthumbnail && (playerType == PlayerType.Essential)) {
                        Box(
                            contentAlignment = Alignment.Center,
                            /*modifier = Modifier
                            .weight(1f)*/
                            //.padding(vertical = 10.dp)
                        ) {
                            if ((!isShowingLyrics && !isShowingVisualizer) || (isShowingVisualizer && showvisthumbnail) || (isShowingLyrics && showlyricsthumbnail))
                                thumbnailContent()
                        }
                    }
                    if (isShowingVisualizer && !showvisthumbnail && playerType == PlayerType.Essential) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .pointerInput(Unit) {
                                    detectHorizontalDragGestures(
                                        onHorizontalDrag = { _, dragAmount ->
                                            deltaX = dragAmount
                                        },
                                        onDragStart = {
                                        },
                                        onDragEnd = {
                                            if (!disablePlayerHorizontalSwipe && playerType == PlayerType.Essential) {
                                                if (deltaX > 5) {
                                                    binder.player.playPrevious()
                                                } else if (deltaX < -5) {
                                                    binder.player.playNext()
                                                }

                                            }

                                        }

                                    )
                                }
                        ) {
                            NextVisualizer(
                                    isDisplayed = isShowingVisualizer
                                )
                        }
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .navigationBarsPadding()
                    ){
                        if (!showlyricsthumbnail) {
                            Lyrics(
                                mediaId = mediaItem.mediaId,
                                isDisplayed = isShowingLyrics,
                                onDismiss = {
                                        isShowingLyrics = false
                                },
                                ensureSongInserted = { Database.insertIgnore( mediaItem ) },
                                size = 1000.dp,
                                mediaMetadataProvider = mediaItem::mediaMetadata,
                                durationProvider = player::getDuration,
                                isLandscape = isLandscape,
                                clickLyricsText = clickLyricsText,
                                modifier = Modifier
                                    .pointerInput(Unit) {
                                        detectHorizontalDragGestures(
                                            onHorizontalDrag = { _, dragAmount ->
                                                deltaX = dragAmount
                                            },
                                            onDragStart = {
                                            },
                                            onDragEnd = {
                                                if (!disablePlayerHorizontalSwipe) {
                                                    if (deltaX > 5) {
                                                        binder.player.playPrevious()
                                                    } else if (deltaX < -5) {
                                                        binder.player.playNext()
                                                    }

                                                }

                                            }

                                        )
                                    }
                            )
                        }
                    }
                }
                Column (
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    if (playerType == PlayerType.Modern) {
                        BoxWithConstraints(
                             contentAlignment = Alignment.Center,
                             modifier = Modifier
                                 .weight(1f)
                             /*modifier = Modifier
                            .weight(1f)*/
                             //.padding(vertical = 10.dp)
                         ) {
                             if ( showthumbnail && !isShowingVisualizer ) {
                                 val fling = PagerDefaults.flingBehavior(state = pagerState,snapPositionalThreshold = 0.25f)
                                 val pageSpacing = thumbnailSpacingL.toInt()*0.01*(screenWidth) - (2.5*playerThumbnailSizeL.size.dp)

                                 LaunchedEffect(pagerState, binder.player.currentMediaItemIndex) {
                                     if (appRunningInBackground || isShowingLyrics) {
                                         pagerState.scrollToPage(binder.player.currentMediaItemIndex)
                                     } else {
                                         pagerState.animateScrollToPage(binder.player.currentMediaItemIndex)
                                     }
                                 }

                                 LaunchedEffect(pagerState) {
                                     var previousPage = pagerState.settledPage
                                     snapshotFlow { pagerState.settledPage }.distinctUntilChanged().collect {
                                         if ( previousPage != it && it != binder.player.currentMediaItemIndex )
                                             binder.player.playAtIndex(it)
                                         previousPage = it
                                     }
                                 }
                                 HorizontalPager(
                                     state = pagerState,
                                     pageSize = PageSize.Fixed( Dimensions.thumbnails.player.song ),
                                     pageSpacing = thumbnailSpacingL.toInt()*0.01*(screenWidth) - (2.5*playerThumbnailSizeL.size.dp),
                                     contentPadding = PaddingValues(start = ((maxWidth - maxHeight)/2).coerceAtLeast(0.dp), end = ((maxWidth - maxHeight)/2 + if (pageSpacing < 0.dp) (-(pageSpacing)) else 0.dp).coerceAtLeast(0.dp)),
                                     beyondViewportPageCount = 3,
                                     flingBehavior = fling,
                                     userScrollEnabled = !disablePlayerHorizontalSwipe,
                                     modifier = Modifier
                                         .padding(
                                             all = (if (thumbnailType == ThumbnailType.Modern) -(10.dp) else 0.dp).coerceAtLeast(
                                                 0.dp
                                             )
                                         )
                                         .conditional(fadingedge) {horizontalFadingEdge()}
                                     ) {

                                     val coverPainter = ImageCacheFactory.Painter(
                                         binder.player.getMediaItemAt( it ).mediaMetadata.artworkUri.toString()
                                     )

                                     val coverModifier = Modifier
                                         .aspectRatio(1f)
                                         .padding(all = playerThumbnailSizeL.size.dp)
                                         .graphicsLayer {
                                             val pageOffSet =
                                                 ((pagerState.currentPage - it) + pagerState.currentPageOffsetFraction).absoluteValue
                                             alpha = lerp(
                                                 start = 0.9f,
                                                 stop = 1f,
                                                 fraction = 1f - pageOffSet.coerceIn(0f,1f)
                                             )
                                             scaleY = lerp(
                                                 start = 0.85f,
                                                 stop = 1f,
                                                 fraction = 1f - pageOffSet.coerceIn(0f,5f)
                                             )
                                             scaleX = lerp(
                                                 start = 0.85f,
                                                 stop = 1f,
                                                 fraction = 1f - pageOffSet.coerceIn(0f,5f)
                                             )
                                         }
                                         .conditional(thumbnailType == ThumbnailType.Modern) {
                                             padding(
                                                 all = 10.dp
                                             )
                                         }
                                         .conditional(thumbnailType == ThumbnailType.Modern) {
                                             doubleShadowDrop(
                                                 if (showCoverThumbnailAnimation) CircleShape else thumbnailRoundness.shape,
                                                 4.dp,
                                                 8.dp
                                             )
                                         }
                                         .clip(thumbnailRoundness.shape)
                                         .combinedClickable(
                                             interactionSource = remember { MutableInteractionSource() },
                                             indication = null,
                                             onClick = {
                                                 if (it == pagerState.settledPage && thumbnailTapEnabled) {
                                                     if (isShowingVisualizer) isShowingVisualizer =
                                                         false
                                                     isShowingLyrics = !isShowingLyrics
                                                 }
                                                 if (it != pagerState.settledPage) {
                                                     binder.player.playAtIndex(it)
                                                 }
                                             },
                                             onLongClick = {
                                                 if (it == pagerState.settledPage)
                                                     showThumbnailOffsetDialog = true
                                             }
                                         )

                                     val zIndex = remember( it, pagerState.currentPage ) {
                                         when {
                                             it == pagerState.currentPage                                             -> 1f
                                             it == (pagerState.currentPage + 1) || it == (pagerState.currentPage - 1) -> .85f
                                             it == (pagerState.currentPage + 2) || it == (pagerState.currentPage - 2) -> .78f
                                             it == (pagerState.currentPage + 3) || it == (pagerState.currentPage - 3) -> .73f
                                             it == (pagerState.currentPage + 4) || it == (pagerState.currentPage - 4) -> .68f
                                             it == (pagerState.currentPage + 5) || it == (pagerState.currentPage - 5) -> .63f
                                             else                                                                     -> .57f
                                         }
                                     }

                                     if (showCoverThumbnailAnimation)
                                         RotateThumbnailCoverAnimationModern(
                                             painter = coverPainter,
                                             isSongPlaying = player.isPlaying,
                                             modifier = coverModifier.zIndex(zIndex),
                                             state = pagerState,
                                             it = it,
                                             imageCoverSize = imageCoverSize,
                                             type = coverThumbnailAnimation
                                         )
                                     else
                                         Box( Modifier.zIndex( zIndex )) {
                                             Image(
                                                 painter = coverPainter,
                                                 contentDescription = "",
                                                 contentScale = ContentScale.Fit,
                                                 modifier = coverModifier
                                             )
                                             if (isDragged && it == binder.player.currentMediaItemIndex) {
                                                 Box(modifier = Modifier
                                                     .align(Alignment.Center)
                                                     .matchParentSize()
                                                 ) {
                                                     NowPlayingSongIndicator(
                                                         binder.player.getMediaItemAt(
                                                             binder.player.currentMediaItemIndex
                                                         ).mediaId, binder.player,
                                                         Dimensions.thumbnails.album
                                                     )
                                                 }
                                             }
                                         }
                                 }
                             }
                            if (isShowingVisualizer) {
                                Box(
                                    modifier = Modifier
                                        .pointerInput(Unit) {
                                            detectHorizontalDragGestures(
                                                onHorizontalDrag = { _, dragAmount ->
                                                    deltaX = dragAmount
                                                },
                                                onDragStart = {
                                                },
                                                onDragEnd = {
                                                    if (!disablePlayerHorizontalSwipe && playerType == PlayerType.Essential) {
                                                        if (deltaX > 5) {
                                                            binder.player.playPrevious()
                                                        } else if (deltaX < -5) {
                                                            binder.player.playNext()
                                                        }

                                                    }

                                                }

                                            )
                                        }
                                ) {
                                    NextVisualizer(
                                        isDisplayed = isShowingVisualizer
                                    )
                                }
                            }
                        }
                    }
                    if (playerType == PlayerType.Essential || isShowingVisualizer) {
                        Controller(
                            mediaItem,
                            Modifier.padding(vertical = 8.dp)
                                    .conditional( playerType == PlayerType.Essential ) {
                                        fillMaxHeight().weight( 1f )
                                    }
                        )
                    } else {
                        val index = (
                            if (!showthumbnail) {
                                if (pagerStateFS.currentPage > binder.player.currentTimeline.windowCount)
                                    0
                                else
                                    pagerStateFS.currentPage
                            } else if (pagerState.currentPage > binder.player.currentTimeline.windowCount) {
                                0
                            } else
                                pagerState.currentPage
                        ).coerceIn( 0, player.mediaItemCount - 1 )

                        Controller(
                            player.getMediaItemAt(index),
                            Modifier.padding( vertical = 8.dp )
                        )
                    }
                    if (!showthumbnail || playerType == PlayerType.Modern) {
                        StatsForNerds(
                            mediaId = mediaItem.mediaId,
                            isDisplayed = statsfornerds,
                            onDismiss = {}
                        )
                    }
                    ActionsBar()
                }
            }
         }
        } else {
           Box {
               if (playerBackgroundColors == PlayerBackgroundColors.BlurredCoverColor && playerType == PlayerType.Modern && (!showthumbnail || albumCoverRotation)) {
                    val fling = PagerDefaults.flingBehavior(
                        state = pagerStateFS,
                        snapPositionalThreshold = 0.30f
                    )
                   val scaleAnimationFloat by animateFloatAsState(
                       if (isDraggedFS) 0.85f else 1f, label = ""
                   )
                   pagerStateFS.LaunchedEffectScrollToPage(binder.player.currentMediaItemIndex)

                    LaunchedEffect(pagerStateFS) {
                        var previousPage = pagerStateFS.settledPage
                        snapshotFlow { pagerStateFS.settledPage }.distinctUntilChanged().collect {
                            if (previousPage != it) {
                                delay(if (swipeAnimationNoThumbnail == SwipeAnimationNoThumbnail.Fade) 0
                                      else 400)
                                if (it != binder.player.currentMediaItemIndex) binder.player.playAtIndex(it)
                            }
                            previousPage = it
                        }
                    }
                    HorizontalPager(
                        state = pagerStateFS,
                        beyondViewportPageCount = if (swipeAnimationNoThumbnail != SwipeAnimationNoThumbnail.Circle || albumCoverRotation && (isShowingLyrics || showthumbnail)) 1 else 0,
                        flingBehavior = fling,
                        userScrollEnabled = !(albumCoverRotation && (isShowingLyrics || showthumbnail)),
                        modifier = Modifier
                            .background(colorPalette().background1)
                            .pointerInteropFilter {
                                circleOffsetY = it.y
                                false
                            }
                    ) {

                        var currentRotation by remember {
                            mutableFloatStateOf(0f)
                        }

                        val rotation = remember {
                            Animatable(currentRotation)
                        }

                        LaunchedEffect(player.isPlaying, pagerStateFS.settledPage) {
                            if (player.isPlaying && it == pagerStateFS.settledPage) {
                                rotation.animateTo(
                                    targetValue = currentRotation + 360f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(30000, easing = LinearEasing),
                                        repeatMode = RepeatMode.Restart
                                    )
                                ) {
                                    currentRotation = value
                                }
                            } else {
                                if (currentRotation > 0f && it == pagerStateFS.settledPage) {
                                    rotation.animateTo(
                                        targetValue = currentRotation + 10,
                                        animationSpec = tween(
                                            1250,
                                            easing = LinearOutSlowInEasing
                                        )
                                    ) {
                                        currentRotation = value
                                    }
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .conditional(albumCoverRotation && (isShowingLyrics || showthumbnail)) {
                                    zIndex(if (it == pagerStateFS.currentPage) 1f else 0.9f)
                                }
                                .conditional(swipeAnimationNoThumbnail == SwipeAnimationNoThumbnail.Scale && isDraggedFS) {
                                    graphicsLayer {
                                        scaleY = scaleAnimationFloat
                                        scaleX = scaleAnimationFloat
                                    }
                                }
                        ) {
                            BlurredCover(
                                thumbnailUrl = binder.player.getMediaItemAt(it).mediaMetadata.artworkUri.toString(),
                                blurAdjuster = blurAdjuster,
                                showThumbnail = showthumbnail,
                                noBlur = noblur,
                                isShowingLyrics = isShowingLyrics,
                                isShowingVisualizer = isShowingVisualizer,
                                contentScale = if (albumCoverRotation && (isShowingLyrics || showthumbnail)) ContentScale.Fit else ContentScale.Crop,
                                modifier = Modifier.conditional(albumCoverRotation) {
                                                       graphicsLayer {
                                                           scaleX = if (isShowingLyrics || showthumbnail) (screenHeight / screenWidth) + 0.5f else 1f
                                                           scaleY = if (isShowingLyrics || showthumbnail) (screenHeight / screenWidth) + 0.5f else 1f
                                                           rotationZ = if ((it == pagerStateFS.settledPage) && (isShowingLyrics || showthumbnail)) rotation.value else 0f
                                                       }
                                                   }
                                                   .conditional(swipeAnimationNoThumbnail == SwipeAnimationNoThumbnail.Fade && !showthumbnail){
                                                       graphicsLayer {
                                                           val pageOffset = pagerStateFS.currentPageOffsetFraction
                                                           translationX = pageOffset * size.width
                                                           alpha = 1 - pageOffset.absoluteValue
                                                       }
                                                   }
                                                   .conditional(swipeAnimationNoThumbnail == SwipeAnimationNoThumbnail.Carousel && isDraggedFS) { //by sinasamaki
                                                       graphicsLayer {
                                                           val startOffset = pagerStateFS.startOffsetForPage(it)
                                                           translationX = size.width * (startOffset * .99f)
                                                           alpha = (2f - startOffset) / 2f
                                                           val blur = (startOffset * 20f).coerceAtLeast(0.1f)
                                                           renderEffect = RenderEffect
                                                               .createBlurEffect(
                                                                   blur, blur, android.graphics.Shader.TileMode.DECAL
                                                               ).asComposeRenderEffect()
                                                           val scale = 1f - (startOffset * .1f)
                                                           scaleX = scale
                                                           scaleY = scale
                                                       }
                                                   }
                                                   .conditional(swipeAnimationNoThumbnail == SwipeAnimationNoThumbnail.Circle && !showthumbnail){ //by sinasamaki
                                                       graphicsLayer {
                                                           val pageOffset = pagerStateFS.offsetForPage(it)
                                                           translationX = size.width * pageOffset

                                                           val endOffset = pagerStateFS.endOffsetForPage(it)
                                                           shadowElevation = 20f

                                                           shape = CirclePath(
                                                               progress = 1f - endOffset.absoluteValue,
                                                               origin = Offset(
                                                                   size.width,
                                                                   circleOffsetY,
                                                               )
                                                           )

                                                           clip = true

                                                           val absoluteOffset = pagerStateFS.offsetForPage(it).absoluteValue
                                                           val scale = 1f + (absoluteOffset.absoluteValue * .4f)

                                                           scaleX = scale
                                                           scaleY = scale

                                                           val startOffset = pagerStateFS.startOffsetForPage(it)
                                                           alpha = (2f - startOffset) / 2f
                                                       }
                                                   }
                                                   .clip(RoundedCornerShape(20.dp))
                                                   .combinedClickable(
                                                       interactionSource = remember { MutableInteractionSource() },
                                                       indication = null,
                                                       onClick = {
                                                           if (thumbnailTapEnabled && !showthumbnail) {
                                                               if (isShowingVisualizer) isShowingVisualizer = false
                                                               isShowingLyrics = !isShowingLyrics
                                                           }
                                                       },
                                                       onDoubleClick = {
                                                           if (!showlyricsthumbnail && !showvisthumbnail)
                                                               showthumbnail = !showthumbnail
                                                       },
                                                       onLongClick = {
                                                           blurAdjuster.isActive = showthumbnail || (isShowingLyrics && !isShowingVisualizer) || !noblur
                                                       }
                                                   )
                            )

                            if ((swipeAnimationNoThumbnail == SwipeAnimationNoThumbnail.Scale) && isDraggedFS){
                                Column {
                                    Spacer(modifier = Modifier
                                        .conditional((screenWidth <= (screenHeight / 2)) && (showlyricsthumbnail || (!expandedplayer && !isShowingLyrics))) {
                                            height(screenWidth)}
                                        .conditional((screenWidth > (screenHeight / 2)) || expandedplayer || (isShowingLyrics && !showlyricsthumbnail)) {weight(1f)})

                                    Box(modifier = Modifier
                                        .conditional(!expandedplayer && (!isShowingLyrics || showlyricsthumbnail)) {weight(1f)}
                                    ) {
                                        Controls(
                                            navController = navController,
                                            onCollapse = onDismiss,
                                            expandedplayer = expandedplayer,
                                            titleExpanded = titleExpanded,
                                            timelineExpanded = timelineExpanded,
                                            controlsExpanded = controlsExpanded,
                                            isShowingLyrics = isShowingLyrics,
                                            media = mediaItem.toUiMedia(positionAndDuration.second),
                                            mediaId = mediaItem.mediaId,
                                            title = cleanPrefix( player.getMediaItemAt(it).mediaMetadata.title.toString() ),
                                            artist = cleanPrefix( player.getMediaItemAt(it).mediaMetadata.artist.toString() ),
                                            artistIds = artistInfos,
                                            albumId = albumId,
                                            shouldBePlaying = shouldBePlaying,
                                            position = positionAndDuration.first,
                                            duration = positionAndDuration.second,
                                            modifier = Modifier
                                                .padding(vertical = 4.dp)
                                                .fillMaxWidth(),
                                            onBlurScaleChange = { blurAdjuster.strength = it },
                                            isExplicit = mediaItem.isExplicit
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Column(modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                0.0f to Color.Transparent,
                                1.0f to if (bottomgradient) if (colorPaletteMode == ColorPaletteMode.Light) Color.White.copy(
                                    if (isLandscape) 0.8f else 0.75f
                                ) else Color.Black.copy(if (isLandscape) 0.8f else 0.75f) else Color.Transparent,
                                startY = if (isLandscape) 600f else if (expandedplayer) 1300f else 950f,
                                endY = POSITIVE_INFINITY
                            )
                        )
                        .background(
                            if (bottomgradient) if (isLandscape) if (colorPaletteMode == ColorPaletteMode.Light) Color.White.copy(
                                0.25f
                            ) else Color.Black.copy(0.25f) else Color.Transparent else Color.Transparent
                        )){}
                }

               BlurredCover(
                   thumbnailUrl = binder.player.mediaMetadata.artworkUri.toString(),
                   blurAdjuster = blurAdjuster,
                   showThumbnail = showthumbnail,
                   noBlur = noblur,
                   isShowingLyrics = isShowingLyrics,
                   isShowingVisualizer = isShowingVisualizer,
                   contentScale = ContentScale.FillHeight
               )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = containerModifier
                    //.padding(top = 10.dp)
                    .drawBehind {
                        if (backgroundProgress == BackgroundProgress.Both || backgroundProgress == BackgroundProgress.Player) {
                            drawRect(
                                color = color.favoritesOverlay,
                                topLeft = Offset.Zero,
                                size = Size(
                                    width = positionAndDuration.first.toFloat() /
                                            positionAndDuration.second.absoluteValue * size.width,
                                    height = size.maxDimension
                                )
                            )
                        }
                    }
            ) {


                if (showTopActionsBar) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .padding(
                                windowInsets
                                    .only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
                                    .asPaddingValues()
                            )
                            //.padding(top = 5.dp)
                            .fillMaxWidth(0.9f)
                            .height(30.dp)
                    ) {

                            Image(
                                painter = painterResource(R.drawable.chevron_down),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(colorPalette().collapsedPlayerProgressBar),
                                modifier = Modifier
                                    .clickable {
                                        onDismiss()
                                    }
                                    .rotate(rotationAngle)
                                    //.padding(10.dp)
                                    .size(24.dp)
                            )

                        Image(
                            bitmap = APP_ICON_IMAGE_BITMAP,
                            contentDescription = "app icon in player",
                            modifier = Modifier.size( 24.dp )
                                               .clickable {
                                                   onDismiss()
                                                   NavRoutes.home.navigateHere( navController )
                                               }
                        )

                            if (!showButtonPlayerMenu)
                                Image(
                                    painter = painterResource(R.drawable.ellipsis_vertical),
                                    contentDescription = null,
                                    colorFilter = ColorFilter.tint(colorPalette().collapsedPlayerProgressBar),
                                    modifier = Modifier
                                        .clickable {
                                            menuState.display {
                                                PlayerMenu(
                                                    navController = navController,
                                                    onDismiss = menuState::hide,
                                                    mediaItem = mediaItem,
                                                    binder = binder,
                                                    onClosePlayer = {
                                                        onDismiss()
                                                    },
                                                    disableScrollingText = disableScrollingText
                                                )
                                            }
                                        }
                                        .rotate(rotationAngle)
                                        //.padding(10.dp)
                                        .size(24.dp)

                                )

                    }
                    Spacer(
                        modifier = Modifier
                            .height(5.dp)
                            .padding(
                                windowInsets
                                    .only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
                                    .asPaddingValues()
                            )
                    )
                }

                if (topPadding && !showTopActionsBar) {
                    Spacer(
                        modifier = Modifier
                            .padding(
                                windowInsets
                                    .only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
                                    .asPaddingValues()
                            )
                            .height(35.dp)
                    )
                }

                BoxWithConstraints(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .conditional((screenWidth <= (screenHeight / 2)) && (showlyricsthumbnail || (!expandedplayer && !isShowingLyrics))) {height(screenWidth)}
                        .conditional((screenWidth > (screenHeight / 2)) || expandedplayer || (isShowingLyrics && !showlyricsthumbnail)) {weight(1f)}
                ) {

                      if (showthumbnail) {
                         if ((!isShowingLyrics && !isShowingVisualizer) || (isShowingVisualizer && showvisthumbnail) || (isShowingLyrics && showlyricsthumbnail)) {
                             if (playerType == PlayerType.Modern) {
                                 val fling = PagerDefaults.flingBehavior(state = pagerState,snapPositionalThreshold = 0.25f)

                                 pagerState.LaunchedEffectScrollToPage(binder.player.currentMediaItemIndex)

                                 LaunchedEffect(pagerState) {
                                     var previousPage = pagerState.settledPage
                                     snapshotFlow { pagerState.settledPage }.distinctUntilChanged().collect {
                                         if ( previousPage != it && it != binder.player.currentMediaItemIndex )
                                             binder.player.playAtIndex(it)
                                         previousPage = it
                                     }
                                 }

                                 val pageSpacing = (thumbnailSpacing.toInt()*0.01*(screenHeight) - if (carousel) (3*carouselSize.size.dp) else (2*playerThumbnailSize.size.dp))
                                 val animatePageSpacing by animateDpAsState(
                                     if (expandedplayer) (thumbnailSpacing.toInt()*0.01*(screenHeight) - if (carousel) (3*carouselSize.size.dp) else (2*carouselSize.size.dp)) else 10.dp,
                                     label = ""
                                 )

                                 val animatePadding by animateDpAsState(
                                     if (expandedplayer) carouselSize.size.dp else playerThumbnailSize.size.dp
                                 )
                                 VerticalPager(
                                     state = pagerState,
                                     pageSize = PageSize.Fixed( if (maxWidth < maxHeight) maxWidth else maxHeight),
                                     contentPadding = PaddingValues(
                                         top = (maxHeight - (if (maxWidth < maxHeight) maxWidth else maxHeight))/2,
                                         bottom = (maxHeight - (if (maxWidth < maxHeight) maxWidth else maxHeight))/2 + if (pageSpacing < 0.dp) (-(pageSpacing)) else 0.dp
                                     ),
                                     pageSpacing = animatePageSpacing,
                                     beyondViewportPageCount = 2,
                                     flingBehavior = fling,
                                     userScrollEnabled = expandedplayer || !disablePlayerHorizontalSwipe,
                                     modifier = Modifier
                                         .padding(
                                             all = (if (expandedplayer) 0.dp else if (thumbnailType == ThumbnailType.Modern) -(10.dp) else 0.dp).coerceAtLeast(
                                                 0.dp
                                             )
                                         )
                                         .conditional(fadingedge) {
                                             VerticalfadingEdge2(fade = (if (expandedplayer) thumbnailFadeEx else thumbnailFade)*0.05f,showTopActionsBar,topPadding,expandedplayer)
                                         }
                                 ){

                                     val coverPainter = ImageCacheFactory.Painter(
                                         binder.player.getMediaItemAt(it).mediaMetadata.artworkUri.toString()
                                     )

                                     val coverModifier = Modifier
                                         .aspectRatio(1f)
                                         .padding(all = animatePadding)
                                         .conditional(carousel)
                                         {
                                             graphicsLayer {
                                                 val pageOffSet =
                                                     ((pagerState.currentPage - it) + pagerState.currentPageOffsetFraction).absoluteValue
                                                 alpha = lerp(
                                                     start = 0.9f,
                                                     stop = 1f,
                                                     fraction = 1f - pageOffSet.coerceIn(0f, 1f)
                                                 )
                                                 scaleY = lerp(
                                                     start = 0.9f,
                                                     stop = 1f,
                                                     fraction = 1f - pageOffSet.coerceIn(0f, 5f)
                                                 )
                                                 scaleX = lerp(
                                                     start = 0.9f,
                                                     stop = 1f,
                                                     fraction = 1f - pageOffSet.coerceIn(0f, 5f)
                                                 )
                                             }
                                         }
                                         .conditional(thumbnailType == ThumbnailType.Modern) {
                                             padding(
                                                 all = 10.dp
                                             )
                                         }
                                         .conditional(thumbnailType == ThumbnailType.Modern) {
                                             doubleShadowDrop(
                                                 if (showCoverThumbnailAnimation) CircleShape else thumbnailRoundness.shape,
                                                 4.dp,
                                                 8.dp
                                             )
                                         }
                                         .clip(thumbnailRoundness.shape)
                                         .combinedClickable(
                                             interactionSource = remember { MutableInteractionSource() },
                                             indication = null,
                                             onClick = {
                                                 if (it == pagerState.settledPage && thumbnailTapEnabled) {
                                                     if (isShowingVisualizer) isShowingVisualizer =
                                                         false
                                                     isShowingLyrics = !isShowingLyrics
                                                 }
                                                 if (it != pagerState.settledPage) {
                                                     binder.player.playAtIndex(it)
                                                 }
                                             },
                                             onLongClick = {
                                                 if (it == pagerState.settledPage && (expandedplayer || fadingedge))
                                                     showThumbnailOffsetDialog = true
                                             }
                                         )

                                     val zIndex = remember( it, pagerState.currentPage ) {
                                         when {
                                             it == pagerState.currentPage                                             -> 1f
                                             it == (pagerState.currentPage + 1) || it == (pagerState.currentPage - 1) -> .85f
                                             it == (pagerState.currentPage + 2) || it == (pagerState.currentPage - 2) -> .78f
                                             it == (pagerState.currentPage + 3) || it == (pagerState.currentPage - 3) -> .73f
                                             it == (pagerState.currentPage + 4) || it == (pagerState.currentPage - 4) -> .68f
                                             it == (pagerState.currentPage + 5) || it == (pagerState.currentPage - 5) -> .63f
                                             else                                                                     -> .57f
                                         }
                                     }

                                     if (showCoverThumbnailAnimation)
                                         RotateThumbnailCoverAnimationModern(
                                             painter = coverPainter,
                                             isSongPlaying = player.isPlaying,
                                             modifier = coverModifier.zIndex( zIndex ),
                                             state = pagerState,
                                             it = it,
                                             imageCoverSize = imageCoverSize,
                                             type = coverThumbnailAnimation
                                         )
                                     else
                                         Box( Modifier.zIndex( zIndex ) ) {
                                             Image(
                                                 painter = coverPainter,
                                                 contentDescription = "",
                                                 contentScale = ContentScale.Fit,
                                                 modifier = coverModifier
                                             )
                                             if (isDragged && expandedplayer && it == binder.player.currentMediaItemIndex) {
                                                 Box(modifier = Modifier
                                                     .align(Alignment.Center)
                                                     .matchParentSize()
                                                 ) {
                                                     NowPlayingSongIndicator(
                                                         binder.player.getMediaItemAt(
                                                             binder.player.currentMediaItemIndex
                                                         ).mediaId, binder.player,
                                                         Dimensions.thumbnails.album
                                                     )
                                                 }
                                             }
                                         }
                                 }
                             } else {
                                 thumbnailContent()
                             }
                         }
                      }

                   Box(
                        modifier = Modifier
                            .pointerInput(Unit) {
                                detectHorizontalDragGestures(
                                    onHorizontalDrag = { _, dragAmount ->
                                        deltaX = dragAmount
                                    },
                                    onDragStart = {
                                    },
                                    onDragEnd = {
                                        if (!disablePlayerHorizontalSwipe) {
                                            if (deltaX > 5) {
                                                binder.player.playPrevious()
                                            } else if (deltaX <-5){
                                                binder.player.playNext()
                                            }

                                        }

                                    }

                                )
                            }
                    ) {
                        if (!showlyricsthumbnail)
                            Lyrics(
                                mediaId = mediaItem.mediaId,
                                isDisplayed = isShowingLyrics,
                                onDismiss = {
                                        isShowingLyrics = false
                                },
                                ensureSongInserted = { Database.insertIgnore( mediaItem ) },
                                size = 1000.dp,
                                mediaMetadataProvider = mediaItem::mediaMetadata,
                                durationProvider = player::getDuration,
                                isLandscape = isLandscape,
                                clickLyricsText = clickLyricsText,
                            )
                        if (!showvisthumbnail)
                            NextVisualizer(
                                isDisplayed = isShowingVisualizer
                            )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                    .conditional(!expandedplayer && (!isShowingLyrics || showlyricsthumbnail)){weight(1f)}
                ){
                if (!expandedplayer || !isShowingLyrics || queueDurationExpanded) {
                    if (showTotalTimeQueue)
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                        ) {
                            Image(
                                painter = painterResource(R.drawable.time),
                                colorFilter = ColorFilter.tint(colorPalette().accent),
                                modifier = Modifier
                                    .size(20.dp)
                                    .padding(horizontal = 5.dp),
                                contentDescription = "Background Image",
                                contentScale = ContentScale.Fit
                            )

                            Box {
                                BasicText(
                                    text = " ${formatAsTime(totalPlayTimes)}",
                                    style = typography().xxs.semiBold.merge(
                                        TextStyle(
                                            textAlign = TextAlign.Center,
                                            color = colorPalette().text,
                                        )
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                BasicText(
                                    text = " ${formatAsTime(totalPlayTimes)}",
                                    style = typography().xxs.semiBold.merge(
                                        TextStyle(
                                            textAlign = TextAlign.Center,
                                            drawStyle = Stroke(
                                                width = 1f,
                                                join = StrokeJoin.Round
                                            ),
                                            color = if (!textoutline) Color.Transparent
                                            else if (colorPaletteMode == ColorPaletteMode.Light ||
                                                (colorPaletteMode == ColorPaletteMode.System && (!isSystemInDarkTheme()))
                                            )
                                                Color.White.copy(0.5f)
                                            else Color.Black,
                                        )
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }


                    Spacer(
                        modifier = Modifier
                            .height(10.dp)
                    )
                }
                Box(modifier = Modifier
                    .conditional(!expandedplayer && (!isShowingLyrics || showlyricsthumbnail)){weight(1f)}) {
                    if (playerType == PlayerType.Essential || isShowingLyrics || isShowingVisualizer) {
                        Controller(
                            mediaItem,
                            Modifier.padding( vertical = 4.dp )
                                    .fillMaxWidth()
                        )
                    } else if (!(swipeAnimationNoThumbnail == SwipeAnimationNoThumbnail.Scale && isDraggedFS)){
                        val index = (
                                if (!showthumbnail) {
                                    if (pagerStateFS.currentPage > binder.player.currentTimeline.windowCount)
                                        0
                                    else
                                        pagerStateFS.currentPage
                                } else if (pagerState.currentPage > binder.player.currentTimeline.windowCount) {
                                    0
                                } else
                                    pagerState.currentPage
                        ).coerceIn( 0, player.mediaItemCount - 1 )

                        Controller(
                            player.getMediaItemAt(index),
                            Modifier.padding( vertical = 4.dp )
                                    .fillMaxWidth()
                        )
                    }
                }

                if (!showthumbnail || playerType == PlayerType.Modern) {
                    if (!isShowingLyrics || statsExpanded) {
                        StatsForNerds(
                            mediaId = mediaItem.mediaId,
                            isDisplayed = statsfornerds,
                            onDismiss = {}
                        )
                    }
                }
                ActionsBar()
              }
            }
           }
        }

        CustomModalBottomSheet(
            showSheet = showQueue,
            onDismissRequest = { showQueue = false },
            containerColor = if (queueType == QueueType.Modern) Color.Transparent else colorPalette().background2,
            contentColor = if (queueType == QueueType.Modern) Color.Transparent else colorPalette().background2,
            modifier = Modifier
                .fillMaxWidth(),
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            dragHandle = {
                Surface(
                    modifier = Modifier.padding(vertical = 0.dp),
                    color = colorPalette().background0,
                    shape = thumbnailShape()
                ) {}
            },
            shape = thumbnailRoundness.shape
        ) {
            Queue(
                navController = navController,
                onDismiss = {
                    queueLoopState.value = it
                    showQueue = false
                },
                onDiscoverClick = {
                    discoverState.value = it
                }
            )
        }

        CustomModalBottomSheet(
            showSheet = showSearchEntity,
            onDismissRequest = { showSearchEntity = false },
            containerColor = if (playerType == PlayerType.Modern) Color.Transparent else colorPalette().background2,
            contentColor = if (playerType == PlayerType.Modern) Color.Transparent else colorPalette().background2,
            modifier = Modifier
                .fillMaxWidth(),
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            dragHandle = {
                Surface(
                    modifier = Modifier.padding(vertical = 0.dp),
                    color = colorPalette().background0,
                    shape = thumbnailShape()
                ) {}
            },
            shape = thumbnailRoundness.shape
        ) {
            SearchYoutubeEntity(
                navController = navController,
                onDismiss = { showSearchEntity = false },
                query = "${mediaItem.mediaMetadata.artist.toString()} - ${mediaItem.mediaMetadata.title.toString()}",
                disableScrollingText = disableScrollingText
            )
        }

    }

}

@Composable
@androidx.annotation.OptIn(UnstableApi::class)
fun PagerState.LaunchedEffectScrollToPage(
    index: Int
) {
    val pagerState = this
    LaunchedEffect(pagerState, index) {
        if (!appRunningInBackground) {
            pagerState.animateScrollToPage(index)
        } else {
            pagerState.scrollToPage(index)
        }
    }
}



