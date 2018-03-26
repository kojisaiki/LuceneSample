package io.saikou9901.LuceneSample;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ja.JapaneseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.*;
import org.springframework.stereotype.Service;

import javax.xml.soap.Text;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class LuceneService  {

    private Directory directory;
    private IndexWriter iwriter;
    private IndexWriterConfig config;
    private Analyzer analyzer;

    public LuceneService() {
        //directory = new RAMDirectory();
        try {
            directory = FSDirectory.open(FileSystems.getDefault().getPath("data", "lucene-index"));
            analyzer = new JapaneseAnalyzer();
        } catch (IOException e) {
            // TODO: 検索できない旨がフロントにわかるように
        }
    }

    private final String FIELD_ID = "id";
    private final String FIELD_NAME = "name";
    private final String FIELD_VALUE = "value";

    public void regist(DocumentDto newdocument) throws Exception {
        try {
            config = new IndexWriterConfig(analyzer);
            iwriter = new IndexWriter(directory, config);

            Document doc = new Document();
            doc.add(new Field(FIELD_ID, newdocument.getId().toString(), StringField.TYPE_STORED));
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
            IndexableField id = hitDoc.getField(FIELD_ID);
            result.add(new DocumentDto(
                    id == null ? null : UUID.fromString(id.stringValue()),
                    hits[i].doc,
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

    public List<DocumentDto> readAll() throws IOException, ParseException {

        DirectoryReader ireader = DirectoryReader.open(directory);
        IndexSearcher isearcher = new IndexSearcher(ireader);

        // クエリの組み立て
        Query query = new QueryParser(FIELD_NAME, analyzer).parse("*:*");

        // 検索実行
        ScoreDoc[] hits = isearcher.search(query, 1000).scoreDocs;
        List<DocumentDto> result = new ArrayList();

        // Iterate through the results:
        for (int i = 0; i < hits.length; i++) {
            Document hitDoc = isearcher.doc(hits[i].doc);
            IndexableField id = hitDoc.getField(FIELD_ID);
            result.add(new DocumentDto(
                    id == null ? null : UUID.fromString(id.stringValue()),
                    hits[i].doc,
                    hitDoc.getField(FIELD_NAME).stringValue(),
                    hitDoc.getField(FIELD_VALUE).stringValue()
            ));
        }

        ireader.close();

        return result;
    }

    public void update(DocumentDto newdocument) throws Exception {
        try {
            Term target = new Term(FIELD_ID, newdocument.getId().toString());

            /**
             * 対象ドキュメントの存在確認
             */
            DirectoryReader ireader = DirectoryReader.open(directory);
            IndexSearcher isearcher = new IndexSearcher(ireader);
            TermQuery query = new TermQuery(target);
            ScoreDoc[] hits = isearcher.search(query, 10).scoreDocs;

            // 1件のヒット以外は失敗
            if (hits.length != 1) {
                throw new Exception("更新対象のドキュメント１件を発見できませんでした。");
            }

            Document subject = isearcher.doc(hits[0].doc);
            newdocument.setDocumentId(hits[0].doc);

            config = new IndexWriterConfig(analyzer);
            iwriter = new IndexWriter(directory, config);

            subject.removeField(FIELD_NAME);
            subject.add(new Field(FIELD_NAME, newdocument.getName(), TextField.TYPE_STORED));
            subject.removeField(FIELD_VALUE);
            subject.add(new Field(FIELD_VALUE, newdocument.getValue(), TextField.TYPE_STORED));

            iwriter.updateDocument(target, subject);

            iwriter.close();
        } catch (IOException e) {
            throw e;
        }
    }

}
