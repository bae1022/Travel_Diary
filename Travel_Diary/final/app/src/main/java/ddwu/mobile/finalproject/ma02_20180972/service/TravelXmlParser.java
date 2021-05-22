package ddwu.mobile.finalproject.ma02_20180972.service;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import ddwu.mobile.finalproject.ma02_20180972.dto.RecommendDto;

public class TravelXmlParser {

    private static String TAG = "TravelXmlParser";
    private enum TagType { NONE, TITLE, ADDRESS, IMG, X, Y};

    //    parsing 대상인 tag를 상수로 선언
    private final static String FAULT_RESULT = "faultResult";

    private final static String TAG_ITEM = "item";
    private final static String TAG_TITLE = "title";
    private final static String TAG_ADDRESS = "addr1";
    private final static String TAG_IMG = "firstimage";
    private final static String TAG_X = "mapx";
    private final static String TAG_Y = "mapy";

    public TravelXmlParser(){

    }

    public ArrayList<RecommendDto> parse(String xml) {
        Log.d(TAG, "PARSER" + xml);

        ArrayList<RecommendDto> resultList = new ArrayList();
        RecommendDto dto = null;

        TagType tagType = TagType.NONE;     //  태그를 구분하기 위한 enum 변수 초기화

        Log.d(TAG, "PARSER" + 1);
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            // 파싱 대상 지정
            parser.setInput(new StringReader(xml));

            int eventType = parser.getEventType();            // 태그 유형 구분 변수 준비

            while (eventType != XmlPullParser.END_DOCUMENT) {

                switch(eventType){
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        String tag = parser.getName();

                        if (tag.equals(TAG_ITEM)) {
                            dto = new RecommendDto();
                        } else if (tag.equals(TAG_TITLE)){
                            tagType = TagType.TITLE;

                        } else if (tag.equals(TAG_ADDRESS)){
                            tagType = TagType.ADDRESS;

                        } else if (tag.equals(TAG_IMG)){
                            tagType = TagType.IMG;

                        } else if (tag.equals(TAG_X)) {
                            tagType = TagType.X;
                        } else if (tag.equals(TAG_Y)){
                            tagType = TagType.Y;
                        } else if (tag.equals(FAULT_RESULT)) {
                            return null;
                        }

                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals(TAG_ITEM)){
                            resultList.add(dto);

                            dto = null;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        switch(tagType){
                            case TITLE:
                                dto.setTitle(parser.getText());
                                break;
                            case ADDRESS:
                                dto.setAddress(parser.getText());
                                break;
                            case IMG:
                                dto.setImg(parser.getText());
                                break;
                            case X:
                                dto.setX(parser.getText());
                                break;
                            case Y:
                                dto.setY(parser.getText());
                        }
                        tagType = tagType.NONE;
                        break;
                }
                eventType = parser.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultList;
    }
}
