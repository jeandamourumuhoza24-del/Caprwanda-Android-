package com.example.domain.engine

data class MusicTrack(
    val id: String,
    val title: String,
    val artist: String,
    val durationText: String,
    val genre: String
)

object AudioEngine {

    val sampleMusicLibrary = listOf(
        MusicTrack("music_1", "Kigali Sunset Afrobeat", "Rwanda Sound Lab", "2:45", "Afrobeat"),
        MusicTrack("music_2", "Intore Drums Anthem", "Traditional Rwanda Ensemble", "1:50", "Cultural"),
        MusicTrack("music_3", "Inyange Acoustic Flow", "Kigali Guitarist", "3:12", "Acoustic"),
        MusicTrack("music_4", "Kivu Chill Lo-Fi Beats", "Akagera Beats", "2:15", "Lo-Fi Ambient"),
        MusicTrack("music_5", "Gorilla Trek Euphoria", "East Africa Vibes", "2:30", "Dance Cinematic")
    )
}
