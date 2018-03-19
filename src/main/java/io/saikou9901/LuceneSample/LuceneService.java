package io.saikou9901.LuceneSample;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ja.JapaneseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class LuceneService  {

    private Analyzer analyzer;
    private Directory directory;
    private IndexWriterConfig config;

    public LuceneService() {
        directory = new RAMDirectory();
        //analyzer = new StandardAnalyzer();
        analyzer = new JapaneseAnalyzer();
        config = new IndexWriterConfig(analyzer);
    }

    private final String FIELD_NAME = "name";
    private final String FIELD_VALUE = "value";

    public void writeData(DocumentDto newdocument) throws Exception {
        try {
            IndexWriter iwriter = new IndexWriter(directory, config);

            Document doc = new Document();

            doc.add(new Field(FIELD_NAME, newdocument.getName(), TextField.TYPE_STORED));
            doc.add(new Field(FIELD_VALUE, newdocument.getValue(), TextField.TYPE_STORED));

            iwriter.addDocument(doc);
            iwriter.close();

        } catch (IOException e) {
            throw e;
        }
    }

    public List<DocumentDto> read(String keyword) throws IOException, ParseException {

        DirectoryReader ireader = DirectoryReader.open(directory);
        IndexSearcher isearcher = new IndexSearcher(ireader);

        // クエリの組み立て
        Query query = structQuery(keyword);

        // 検索実行
        ScoreDoc[] hits = isearcher.search(query, 1000).scoreDocs;
        List<DocumentDto> result = new ArrayList();

        // Iterate through the results:
        for (int i = 0; i < hits.length; i++) {
            Document hitDoc = isearcher.doc(hits[i].doc);
            result.add(new DocumentDto(
                    hitDoc.getField(FIELD_NAME).stringValue(),
                    hitDoc.getField(FIELD_VALUE).stringValue()
                ));
        }

        ireader.close();

        return result;
    }

    private Query structQuery(String keyword) throws ParseException {

        // 複数フィールドに対して検索するには、フィールド毎に生成したQueryをBooleanQueryに集める
        BooleanQuery.Builder container = new BooleanQuery.Builder();

        QueryParser parser;
        Query query;

        // Nameに対するQuery
        parser = new QueryParser(FIELD_NAME, analyzer);
        query = parser.parse(keyword);
        System.out.println("Query for Name => " + query.toString());
        container.add(query, BooleanClause.Occur.SHOULD);

        // Valueに対するQuery
        parser = new QueryParser(FIELD_VALUE, analyzer);
        query = parser.parse(keyword);
        System.out.println("Query for Value => " + query.toString());
        container.add(query, BooleanClause.Occur.SHOULD);

        return container.build();
    }

}
