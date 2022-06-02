package dev.weltonfelix.musy.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@ExperimentalMaterial3Api
@Composable
fun SongList(songs: List<SongObject>) {
	LazyColumn(
		verticalArrangement = Arrangement.spacedBy(16.dp),
	) {
		items(songs) { song ->
			SongCard(song)
		}
		item {
			Spacer(modifier = Modifier.height(4.dp))
		}
	}
}

@ExperimentalMaterial3Api
@Preview(name = "Song list")
@Composable
fun PreviewSongList() {
	SongList(songs = SampleData.sampleSongs)
}

object SampleData {
	val sampleSongs = listOf(
		SongObject(
			title = "Shallow",
			spotifyURL = "https://open.spotify.com/track/3ZQZ9YqeQZ7QZqxz7QX7w4",
			artist = "Lady Gaga",
			durationInSeconds = 240,
			coverImageURL = "https://i.scdn.co/image/ab67616d00001e02e9c9c9c8b8b8b8b8b8b8b8b8b"
		),
		SongObject(
			title = "Love Yourself",
			spotifyURL = "https://open.spotify.com/track/3ZQZ9YqeQZ7QZqxz7QX7w4",
			artist = "Justin Bieber",
			durationInSeconds = 240,
			coverImageURL = "https://i.scdn.co/image/ab67616d00001e02e9c9c9c8b8b8b8b8b8b8b8b8b"
		),
		SongObject(
			title = "Shape of You",
			spotifyURL = "https://open.spotify.com/track/3ZQZ9YqeQZ7QZqxz7QX7w4",
			artist = "Ed Sheeran",
			durationInSeconds = 240,
			coverImageURL = "https://i.scdn.co/image/ab67616d00001e02e9c9c9c8b8b8b8b8b8b8b8b8b"
		),
		SongObject(
			title = "Perfect",
			spotifyURL = "https://open.spotify.com/track/3ZQZ9YqeQZ7QZqxz7QX7w4",
			artist = "Ed Sheeran",
			durationInSeconds = 240,
			coverImageURL = "https://i.scdn.co/image/ab67616d00001e02e9c9c9c8b8b8b8b8b8b8b8b8b"
		),
		SongObject(
			title = "Despacito",
			spotifyURL = "https://open.spotify.com/track/3ZQZ9YqeQZ7QZqxz7QX7w4",
			artist = "Luis Fonsi",
			durationInSeconds = 240,
			coverImageURL = "https://i.scdn.co/image/ab67616d00001e02e9c9c9c8b8b8b8b8b8b8b8b8b"
		),
		SongObject(
			title = "Havana",
			spotifyURL = "https://open.spotify.com/track/3ZQZ9YqeQZ7QZqxz7QX7w4",
			artist = "Camila Cabello",
			durationInSeconds = 240,
			coverImageURL = "https://i.scdn.co/image/ab67616d00001e02e9c9c9c8b8b8b8b8b8b8b8b8b"
		),
	)
}
