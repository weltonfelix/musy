package dev.weltonfelix.musy.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SearchBar(
	modifier: Modifier = Modifier,
	value: String,
	label: String? = null,
	onValueChange: (String) -> Unit,
) {
	TextField(
		value = value,
		label = { if (label != null) Text(label) },
		onValueChange = onValueChange,
		modifier = modifier,
		singleLine = true,
		colors = TextFieldDefaults.textFieldColors(
			focusedLabelColor = MaterialTheme.colorScheme.outline,
		)
	)
}

@Preview(name = "Search Bar")
@Composable
fun PreviewSearchBar() {
	SearchBar(value = "", label = "Type something...", onValueChange = {})
}
