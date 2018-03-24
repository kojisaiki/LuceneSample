package io.saikou9901.LuceneSample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class LuceneResource {

    @Autowired
    private LuceneService luceneService;

    @PostMapping("/lucene")
    private ResponseEntity<DocumentDto> write(@RequestBody DocumentDto document) throws Exception {
        luceneService.writeData(document);
        return ResponseEntity.ok().body(document);
    }

    @GetMapping("/lucene/_search")
    private ResponseEntity<List<DocumentDto>> search(@RequestParam String keyword) throws Exception {
        return ResponseEntity.ok().body(luceneService.read(keyword));
    }

    @GetMapping("/lucene/_all")
    private ResponseEntity<List<DocumentDto>> getAll() throws Exception {
        return ResponseEntity.ok().body(luceneService.readAll());
    }
}
