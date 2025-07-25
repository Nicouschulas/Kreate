package it.fast4x.rimusic.ui.components.themed

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import app.kreate.android.Preferences
import app.kreate.android.R
import it.fast4x.rimusic.Database
import it.fast4x.rimusic.MONTHLY_PREFIX
import it.fast4x.rimusic.PINNED_PREFIX
import it.fast4x.rimusic.cleanPrefix
import it.fast4x.rimusic.colorPalette
import it.fast4x.rimusic.enums.MenuStyle
import it.fast4x.rimusic.enums.NavRoutes
import it.fast4x.rimusic.models.Album
import it.fast4x.rimusic.models.Playlist
import it.fast4x.rimusic.models.PlaylistPreview
import it.fast4x.rimusic.typography
import it.fast4x.rimusic.ui.items.AlbumItem
import it.fast4x.rimusic.ui.styling.Dimensions
import it.fast4x.rimusic.ui.styling.px
import it.fast4x.rimusic.utils.isNetworkConnected
import it.fast4x.rimusic.utils.semiBold
import kotlinx.coroutines.Dispatchers

@ExperimentalTextApi
@SuppressLint("SuspiciousIndentation")
@UnstableApi
@ExperimentalAnimationApi
@Composable
fun AlbumsItemMenu(
    navController: NavController,
    onDismiss: () -> Unit,
    onSelectUnselect: (() -> Unit)? = null,
    onSelect: (() -> Unit)? = null,
    onUncheck: (() -> Unit)? = null,
    onChangeAlbumTitle: (() -> Unit)? = null,
    onChangeAlbumAuthors: (() -> Unit)? = null,
    onChangeAlbumCover: (() -> Unit)? = null,
    onDownloadAlbumCover: (() -> Unit)? = null,
    album: Album,
    modifier: Modifier = Modifier,
    onPlayNext: (() -> Unit)? = null,
    onEnqueue: (() -> Unit)? = null,
    onAddToPlaylist: ((PlaylistPreview) -> Unit)? = null,
    onGoToPlaylist: ((Long) -> Unit)? = null,
    onAddToFavourites: (() -> Unit)? = null,
    disableScrollingText: Boolean
) {
    val density = LocalDensity.current

    var isViewingPlaylists by remember {
        mutableStateOf(false)
    }

    var height by remember {
        mutableStateOf(0.dp)
    }

    val menuStyle by Preferences.MENU_STYLE

    if (menuStyle == MenuStyle.Grid) {
        AlbumsItemGridMenu(
            navController = navController,
            onDismiss = onDismiss,
            album = album,
            onSelectUnselect = onSelectUnselect,
            onChangeAlbumTitle = onChangeAlbumTitle,
            onChangeAlbumAuthors = onChangeAlbumAuthors,
            onChangeAlbumCover = onChangeAlbumCover,
            onDownloadAlbumCover = onDownloadAlbumCover,
            onPlayNext = onPlayNext,
            onEnqueue = onEnqueue,
            onAddToPlaylist = onAddToPlaylist,
            onGoToPlaylist = onGoToPlaylist,
            onAddToFavourites = onAddToFavourites,
            disableScrollingText = disableScrollingText
        )
    } else {
        AnimatedContent(
            targetState = isViewingPlaylists,
            transitionSpec = {
                val animationSpec = tween<IntOffset>(400)
                val slideDirection =
                    if (targetState) AnimatedContentTransitionScope.SlideDirection.Left
                    else AnimatedContentTransitionScope.SlideDirection.Right

                slideIntoContainer(slideDirection, animationSpec) togetherWith
                        slideOutOfContainer(slideDirection, animationSpec)
            }, label = ""
        ) { currentIsViewingPlaylists ->
            if (currentIsViewingPlaylists) {
                val context = LocalContext.current
                val playlistPreviews by remember {
                    Database.playlistTable.sortPreviewsByName()
                }.collectAsState( emptyList(), Dispatchers.IO )

                val pinnedPlaylists = playlistPreviews.filter {
                    it.playlist.name.startsWith(PINNED_PREFIX, 0, true)
                            && if (isNetworkConnected(context)) !(it.playlist.isYoutubePlaylist && !it.playlist.isEditable) else !it.playlist.isYoutubePlaylist
                }

                val youtubePlaylists = playlistPreviews.filter { it.playlist.isEditable && it.playlist.isYoutubePlaylist && !it.playlist.name.startsWith(PINNED_PREFIX) }

                val unpinnedPlaylists = playlistPreviews.filter {
                    !it.playlist.name.startsWith(PINNED_PREFIX, 0, true) &&
                            !it.playlist.name.startsWith(MONTHLY_PREFIX, 0, true) &&
                            !it.playlist.isYoutubePlaylist
                }

                var isCreatingNewPlaylist by rememberSaveable {
                    mutableStateOf(false)
                }

                if (isCreatingNewPlaylist && onAddToPlaylist != null) {
                    InputTextDialog(
                        onDismiss = { isCreatingNewPlaylist = false },
                        title = stringResource(R.string.enter_the_playlist_name),
                        value = "",
                        placeholder = stringResource(R.string.enter_the_playlist_name),
                        setValue = { text ->
                            onDismiss()
                            Database.asyncTransaction {
                                val pId = playlistTable.insert( Playlist(name = text) )
                                onAddToPlaylist(
                                    PlaylistPreview(
                                        Playlist(
                                            id = pId,
                                            name = text
                                        ),
                                        0
                                    )
                                )
                            }
                        }
                    )

                }

                BackHandler {
                    isViewingPlaylists = false
                }

                Menu(
                    modifier = modifier
                        .requiredHeight(height)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = { isViewingPlaylists = false },
                            icon = R.drawable.chevron_back,
                            color = colorPalette().textSecondary,
                            modifier = Modifier
                                .padding(all = 4.dp)
                                .size(20.dp)
                        )

                        if (onAddToPlaylist != null) {
                            SecondaryTextButton(
                                text = stringResource(R.string.new_playlist),
                                onClick = { isCreatingNewPlaylist = true },
                                alternative = true
                            )
                        }
                    }

                    if (pinnedPlaylists.isNotEmpty()) {
                        BasicText(
                            text = stringResource(R.string.pinned_playlists),
                            style = typography().m.semiBold,
                            modifier = modifier.padding(start = 20.dp, top = 5.dp)
                        )

                        onAddToPlaylist?.let { onAddToPlaylist ->
                            pinnedPlaylists.forEach { playlistPreview ->
                                MenuEntry(
                                    icon = R.drawable.add_in_playlist,
                                    text = cleanPrefix(playlistPreview.playlist.name),
                                    secondaryText = "${playlistPreview.songCount} " + stringResource(
                                        R.string.songs
                                    ),
                                    onClick = {
                                        onDismiss()
                                        onAddToPlaylist(
                                            PlaylistPreview(
                                                playlistPreview.playlist,
                                                playlistPreview.songCount
                                            )
                                        )
                                    },
                                    trailingContent = {
                                        if (playlistPreview.playlist.isYoutubePlaylist) {
                                            Image(
                                                painter = painterResource(R.drawable.ytmusic),
                                                contentDescription = null,
                                                colorFilter = ColorFilter.tint(
                                                    Color.Red.copy(0.75f).compositeOver(Color.White)
                                                ),
                                                modifier = Modifier
                                                    .size(18.dp)
                                            )
                                        }
                                        IconButton(
                                            icon = R.drawable.open,
                                            color = colorPalette().text,
                                            onClick = {
                                                if (onGoToPlaylist != null) {
                                                    onGoToPlaylist(playlistPreview.playlist.id)
                                                    onDismiss()
                                                }
                                                NavRoutes.localPlaylist.navigateHere( navController, playlistPreview.playlist.id )
                                            },
                                            modifier = Modifier
                                                .size(24.dp)
                                        )
                                    }
                                )
                            }
                        }
                    }

                    if (youtubePlaylists.isNotEmpty() && isNetworkConnected(context)) {
                        BasicText(
                            text = stringResource(R.string.ytm_playlists),
                            style = typography().m.semiBold,
                            modifier = Modifier.padding(start = 20.dp, top = 5.dp)
                        )

                        onAddToPlaylist?.let { onAddToPlaylist ->
                            youtubePlaylists.forEach { playlistPreview ->
                                MenuEntry(
                                    icon = R.drawable.add_in_playlist,
                                    text = cleanPrefix(playlistPreview.playlist.name),
                                    secondaryText = "${playlistPreview.songCount} " + stringResource(R.string.songs),
                                    onClick = {
                                        onDismiss()
                                        onAddToPlaylist(
                                            PlaylistPreview(
                                                playlistPreview.playlist,
                                                playlistPreview.songCount
                                            )
                                        )
                                    },
                                    trailingContent = {
                                        IconButton(
                                            icon = R.drawable.open,
                                            color = colorPalette().text,
                                            onClick = {
                                                if (onGoToPlaylist != null) {
                                                    onGoToPlaylist(playlistPreview.playlist.id)
                                                    onDismiss()
                                                }
                                                NavRoutes.localPlaylist.navigateHere( navController, playlistPreview.playlist.id )
                                            },
                                            modifier = Modifier
                                                .size(24.dp)
                                        )
                                    }
                                )
                            }
                        }
                    }

                    if (unpinnedPlaylists.isNotEmpty()) {
                        BasicText(
                            text = stringResource(R.string.playlists),
                            style = typography().m.semiBold,
                            modifier = modifier.padding(start = 20.dp, top = 5.dp)
                        )

                        onAddToPlaylist?.let { onAddToPlaylist ->
                            unpinnedPlaylists.forEach { playlistPreview ->
                                MenuEntry(
                                    icon = R.drawable.add_in_playlist,
                                    text = cleanPrefix(playlistPreview.playlist.name),
                                    secondaryText = "${playlistPreview.songCount} " + stringResource(
                                        R.string.songs
                                    ),
                                    onClick = {
                                        onDismiss()
                                        onAddToPlaylist(
                                            PlaylistPreview(
                                                playlistPreview.playlist,
                                                playlistPreview.songCount
                                            )
                                        )
                                    },
                                    trailingContent = {
                                        IconButton(
                                            icon = R.drawable.open,
                                            color = colorPalette().text,
                                            onClick = {
                                                if (onGoToPlaylist != null) {
                                                    onGoToPlaylist(playlistPreview.playlist.id)
                                                    onDismiss()
                                                }
                                                NavRoutes.localPlaylist.navigateHere( navController, playlistPreview.playlist.id )
                                            },
                                            modifier = Modifier
                                                .size(24.dp)
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            } else {
                Menu(
                    modifier = modifier
                        .onPlaced { height = with(density) { it.size.height.toDp() } }
                ) {
                    val thumbnailSizeDp = Dimensions.thumbnails.song + 20.dp
                    val thumbnailSizePx = thumbnailSizeDp.px
                    val thumbnailArtistSizeDp = Dimensions.thumbnails.song + 10.dp
                    val thumbnailArtistSizePx = thumbnailArtistSizeDp.px

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(end = 12.dp)
                    ) {
                        AlbumItem(
                            album = album,
                            thumbnailSizePx = thumbnailSizePx,
                            thumbnailSizeDp = thumbnailSizeDp,
                            yearCentered = false,
                            disableScrollingText = disableScrollingText
                        )

                        /*
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            //icon = if (likedAt == null) R.drawable.heart_outline else R.drawable.heart,
                            icon = R.drawable.heart,
                            //color = colorPalette.favoritesIcon,
                            color = if (likedAt == null) colorPalette.textDisabled else colorPalette.text,
                            onClick = {
                                query {
                                    if (Database.like(
                                            mediaItem.mediaId,
                                            if (likedAt == null) System.currentTimeMillis() else null
                                        ) == 0
                                    ) {
                                        Database.insert(mediaItem, Song::toggleLike)
                                    }
                                }
                            },
                            modifier = Modifier
                                .padding(all = 4.dp)
                                .size(24.dp)
                        )

                        if (!isLocal) IconButton(
                            icon = R.drawable.share_social,
                            color = colorPalette.text,
                            onClick = onShare,
                            modifier = Modifier
                                .padding(all = 4.dp)
                                .size(24.dp)
                        )

                    }
                    */
                    }

                    Spacer(
                        modifier = Modifier
                            .height(8.dp)
                    )

                    onSelectUnselect?.let { onSelectUnselect ->
                        MenuEntry(
                            icon = R.drawable.checked_filled,
                            text = "${stringResource(R.string.item_select)}/${stringResource(R.string.item_deselect)}",
                            onClick = {
                                onDismiss()
                                onSelectUnselect()
                            }
                        )
                    }

                    onSelect?.let { onSelect ->
                        MenuEntry(
                            icon = R.drawable.checked_filled,
                            text = stringResource(R.string.item_select),
                            onClick = {
                                onDismiss()
                                onSelect()
                            }
                        )
                    }
                    /*
                onUncheck?.let { onUncheck ->
                    MenuEntry(
                        icon = R.drawable.unchecked,
                        text = stringResource(R.string.item_uncheck),
                        onClick = {
                            onDismiss()
                            onUncheck()
                        }
                    )
                }
                 */

                    onChangeAlbumTitle?.let {
                        MenuEntry(
                            icon = R.drawable.title_edit,
                            text = stringResource(R.string.update_title),
                            onClick = {
                                onDismiss()
                                onChangeAlbumTitle()
                            }
                        )
                    }
                    onChangeAlbumAuthors?.let {
                        MenuEntry(
                            icon = R.drawable.artists_edit,
                            text = stringResource(R.string.update_authors),
                            onClick = {
                                onDismiss()
                                onChangeAlbumAuthors()
                            }
                        )
                    }
                    onChangeAlbumCover?.let {
                        MenuEntry(
                            icon = R.drawable.cover_edit,
                            text = stringResource(R.string.update_cover),
                            onClick = {
                                onDismiss()
                                onChangeAlbumCover()
                            }
                        )
                    }

                    onDownloadAlbumCover?.let {
                        MenuEntry(
                            icon = R.drawable.download_cover,
                            text = stringResource(R.string.download_cover),
                            onClick = {
                                onDismiss()
                                onDownloadAlbumCover()
                            }
                        )
                    }

                    onPlayNext?.let { onPlayNext ->
                        MenuEntry(
                            icon = R.drawable.play_skip_forward,
                            text = stringResource(R.string.play_next),
                            onClick = {
                                onDismiss()
                                onPlayNext()
                            }
                        )
                    }

                    onEnqueue?.let { onEnqueue ->
                        MenuEntry(
                            icon = R.drawable.enqueue,
                            text = stringResource(R.string.enqueue),
                            onClick = {
                                onDismiss()
                                onEnqueue()
                            }
                        )
                    }


                    if (onAddToPlaylist != null) {
                        MenuEntry(
                            icon = R.drawable.add_in_playlist,
                            text = stringResource(R.string.add_to_playlist),
                            onClick = { isViewingPlaylists = true },
                            trailingContent = {
                                Image(
                                    painter = painterResource(R.drawable.chevron_forward),
                                    contentDescription = null,
                                    colorFilter = ColorFilter.tint(
                                        colorPalette().textSecondary
                                    ),
                                    modifier = Modifier
                                        .size(16.dp)
                                )
                            }
                        )
                    }

                    onAddToFavourites?.let { onAddToFavourites ->
                        MenuEntry(
                            icon = R.drawable.heart,
                            text = stringResource(R.string.add_to_favorites),
                            onClick = {
                                onDismiss()
                                onAddToFavourites()
                            }
                        )
                    }
                }
            }
        }
    }
}