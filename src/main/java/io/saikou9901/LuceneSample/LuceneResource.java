package io.saikou9901.LuceneSample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class LuceneResource {

    @Autowired
    private LuceneService luceneService;

    @PostMapping("/lucene")
    private ResponseEntity<DocumentDto> regist(@RequestParam String name, @RequestParam String value) throws Exception {
        DocumentDto newdata = new DocumentDto(
                UUID.randomUUID(),
                name,
                value
        );
        luceneService.regist(newdata);
        return ResponseEntity.ok().body(newdata);
    }

    @PutMapping("/lucene")
    private ResponseEntity<DocumentDto> update(@RequestParam String id, @RequestParam String name, @RequestParam String value) throws Exception {
        DocumentDto newdata = new DocumentDto(
                UUID.fromString(id),
                name,
                value
        );
        luceneService.update(newdata);
        return ResponseEntity.ok().body(newdata);
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
