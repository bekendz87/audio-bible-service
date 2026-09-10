package com.audiobible.audio_bible_service.config;

import com.audiobible.audio_bible_service.entity.Book;
import com.audiobible.audio_bible_service.entity.Chapter;
import com.audiobible.audio_bible_service.repository.BookRepository;
import com.audiobible.audio_bible_service.repository.ChapterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final BookRepository bookRepository;
    private final ChapterRepository chapterRepository;

    @Override
    public void run(String... args) {
        // if (bookRepository.count() == 0) {
        //     Book sangTheKy = Book.builder()
        //             .id("sang-the-ky")
        //             .title("Sáng Thế Ký")
        //             .subtitle("50 Chương • Khởi đầu sự sáng tạo")
        //             .testament("OLD")
        //             .totalChapters(50)
        //             .artworkUrl(
        //                     "https://upload.wikimedia.org/wikipedia/commons/thumb/f/fc/Pieter_Bruegel_the_Elder_-_The_Tower_of_Babel_%28Vienna%29_-_Google_Art_Project_-_edited.jpg/800px-Pieter_Bruegel_the_Elder_-_The_Tower_of_Babel_%28Vienna%29_-_Google_Art_Project_-_edited.jpg")
        //             .build();

        //     bookRepository.save(sangTheKy);

        //     Chapter ch1 = Chapter.builder()
        //             .id("sang-the-ky-1")
        //             .book(sangTheKy)
        //             .chapterNum(1)
        //             .reader("Giọng đọc Nam")
        //             .audioUrl(
        //                     "https://s3.us-west-1.wasabisys.com/thanhlinh.net/drupal_files/2024-11/1307--khai-huyen---01.mp3")
        //             .duration("05:12")
        //             .build();

        //     chapterRepository.save(ch1);
        // }
    }
}