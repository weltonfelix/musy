package dev.weltonfelix.musy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.centerAlignedTopAppBarColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dev.weltonfelix.musy.services.SpotifyApi
import dev.weltonfelix.musy.ui.components.SearchBar
import dev.weltonfelix.musy.ui.components.SongList
import dev.weltonfelix.musy.ui.components.SongObject
import dev.weltonfelix.musy.ui.theme.MusyTheme
import dev.weltonfelix.musy.util.debounce
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		setContent {
			val backgroundColor = if (isSystemInDarkTheme()) {
				Color(0xFF30343F)
			} else {
				Color(0xFFFAFAFF)
			}

			val systemUiController = rememberSystemUiController()
			SideEffect {
				systemUiController.setNavigationBarColor(
					color = backgroundColor
				)
			}

			MusyTheme {
				var isLoading by remember { mutableStateOf(false) }
				val snackBarHostState = remember { SnackbarHostState() }

				Scaffold(
					snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
					topBar = {
						Box {
							CenterAlignedTopAppBar(
								title = {
									Text("Musy", color = Color(0xFFFAFAFF))
								},
								colors = centerAlignedTopAppBarColors(
									containerColor = MaterialTheme.colorScheme.primary
								)
							)

							if (isLoading) LinearProgressIndicator(
								modifier = Modifier
									.fillMaxWidth()
									.align(Alignment.BottomCenter),
								color = MaterialTheme.colorScheme.tertiary
							)
						}
					}
				) { scaffoldPaddingValues ->
					val focusManager = LocalFocusManager.current
					val coroutineScope = rememberCoroutineScope()

					var searchValue by remember { mutableStateOf("") }
					var songs by remember { mutableStateOf(emptyList<SongObject>()) }

					val spotifyApi = SpotifyApi()
					spotifyApi.auth()

					fun handleSearch(query: String) {
						songs = emptyList()
						if (query.isEmpty()) {
							return
						}

						isLoading = true
						spotifyApi.search(
							query,
							onResponse = { tracks ->
								isLoading = false

								songs = tracks.map {
									SongObject(
										title = it.title,
										artist = it.artist,
										spotifyURL = it.spotifyURL,
										coverImageURL = it.albumCoverURL,
										durationInSeconds = it.durationSeconds
									)
								}
							}, onException = {
								println("deu merda boy: $it")
								isLoading = false
								coroutineScope.launch {
									val result =
										snackBarHostState.showSnackbar(
											"There was an error while searching for songs",
											actionLabel = "Retry"
										)
									if (result == SnackbarResult.ActionPerformed) {
										handleSearch(query)
									}
									return@launch
								}
							})
					}

					val onSearchInputChange: (String) -> Unit = debounce(
						coroutineScope = coroutineScope,
						callback = { handleSearch(it) }
					)

					Column(
						horizontalAlignment = Alignment.CenterHorizontally,
						modifier = Modifier
							.fillMaxSize()
							.padding(scaffoldPaddingValues)
							.padding(top = 32.dp, start = 16.dp, end = 16.dp)
							.pointerInput(Unit) {
								detectTapGestures(onTap = {
									focusManager.clearFocus()
								})
							}
					) {
						SearchBar(
							modifier = Modifier.fillMaxWidth(),
							value = searchValue,
							label = "Search for your favorite songs",
							onValueChange = { newValue ->
								searchValue = newValue
								onSearchInputChange(newValue)
							}
						)

						Spacer(Modifier.height(32.dp))

						if (searchValue.isEmpty()) {
							Text(
								text = "Type a song name to search",
								style = MaterialTheme.typography.bodyLarge,
								fontWeight = FontWeight.SemiBold
							)
						} else if (!isLoading) {
							if (songs.isEmpty()) {
								Text(
									text = "No songs found",
									style = MaterialTheme.typography.bodyLarge,
									fontWeight = FontWeight.SemiBold
								)
							} else {
								SongList(songs)
							}
						}
					}
				}
			}
		}
	}
}
