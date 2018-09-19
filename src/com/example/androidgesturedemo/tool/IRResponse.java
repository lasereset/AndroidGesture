package com.example.androidgesturedemo.tool;

import java.util.HashMap;
import java.util.Map;

import com.dianming.support.Fusion;

import android.annotation.SuppressLint;
import android.util.Log;

public class IRResponse {

    public static class DMExplain {
        private int code;
        private String description;
        private String unicode;
        private String speakDes;

        public DMExplain(int code, String description, String unicode, String speakDes) {
            this.code = code;
            this.description = description;
            this.unicode = unicode;
            this.speakDes = speakDes;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getUnicode() {
            return unicode;
        }

        public void setUnicode(String unicode) {
            this.unicode = unicode;
        }

        public String getSpeakDes() {
            return speakDes;
        }

        public void setSpeakDes(String speakDes) {
            this.speakDes = speakDes;
        }
    }

    @SuppressLint("UseSparseArrays")
    public static final HashMap<Integer, DMExplain> all_dm_hashMap = new HashMap<Integer, DMExplain>();
    static {
        all_dm_hashMap.put(0, new DMExplain(101, "单指单击空格区域", "\u2800", "空方"));
        all_dm_hashMap.put(1, new DMExplain(102, "单指单击1区域", "\u2801", "一点"));
        all_dm_hashMap.put(2, new DMExplain(103, "单指单击2区域", "\u2802", "两点"));
        all_dm_hashMap.put(4, new DMExplain(104, "单指单击4区域", "\u2808", "四点"));
        all_dm_hashMap.put(5, new DMExplain(105, "单指单击5区域", "\u2810", "五点"));
        all_dm_hashMap.put(12, new DMExplain(106, "单指单击 1 区域并滑到 2 区域结束", "\u2803", "一二点"));
        all_dm_hashMap.put(14, new DMExplain(107, "单指单击 1 区域并滑到 4 区域结束", "\u2809", "一四点"));
        all_dm_hashMap.put(15, new DMExplain(108, "单指单击 1 区域并滑到 5 区域结束", "\u2811", "一五点"));
        all_dm_hashMap.put(24, new DMExplain(109, "单指单击 2 区域并滑到4 区域结束", "\u280A", "二四点"));
        all_dm_hashMap.put(25, new DMExplain(110, "单指单击 2 区域并滑到 5 区域结束", "\u2812", "二五点"));
        all_dm_hashMap.put(45, new DMExplain(111, "单指单击 4 区域并滑到 5 区域结束", "\u2818", "四五点"));
        all_dm_hashMap.put(124, new DMExplain(112, "单指单击 1 区域,滑到 2 区域再转向 4区域结束", "\u280B", "一二四点"));
        all_dm_hashMap.put(125, new DMExplain(113, "单指单击 1 区域,滑到 2 区域再转向 5区域结束", "\u2813", "一二五点"));
        all_dm_hashMap.put(145, new DMExplain(114, "单指单击 1 区域,滑到 4 区域再转向 5区域结束", "\u2819", "一四五点"));
        all_dm_hashMap.put(245, new DMExplain(115, "单指单击 2 区域,滑到 4 区域再转向 5区域结束", "\u281A", "二四五点"));
        all_dm_hashMap.put(1245, new DMExplain(116, "单指单击 1 区域,滑到 2 区域,转向 4区域在滑到 5 区域结束", "\u281B", "一二四五点"));
        
        all_dm_hashMap.put(30, new DMExplain(201, "单指触摸 3 区域 2 秒", "\u2804", "三点"));
        all_dm_hashMap.put(301, new DMExplain(202, "单指单击 1 区域,另外一只手指同时触摸压住 3 区域", "\u2805", "一三点"));
        all_dm_hashMap.put(302, new DMExplain(203, "单指单击 2 区域,另外一只手指同时触摸压住 3 区域", "\u2806", "二三点"));
        all_dm_hashMap.put(304, new DMExplain(204, "单指单击 4 区域,另外一只手指同时触摸压住 3 区域", "\u280C", "三四点"));
        all_dm_hashMap.put(305, new DMExplain(205, "单指单击 5 区域,另外一只手指同时触摸压住 3 区域", "\u2814", "三五点"));
        all_dm_hashMap.put(3012, new DMExplain(206, "单指单击 1 区域并滑到 2 区域结束,另外一只手指同时触摸压住 3 区域", "\u2807", "一二三点"));
        all_dm_hashMap.put(3014, new DMExplain(207, "单指单击 1 区域并滑到 4 区域结束,另外一只手指同时触摸压住 3 区域", "\u280D", "一四三点"));
        all_dm_hashMap.put(3015, new DMExplain(208, "单指单击 1 区域并滑到 5 区域结束,另外一只手指同时触摸压住 3 区域", "\u2815", "一三五点"));
        all_dm_hashMap.put(3024, new DMExplain(209, "单指单击 2 区域并滑到 4 区域结束,另外一只手指同时触摸压住 3 区域", "\u280E", "二三四点"));
        all_dm_hashMap.put(3025, new DMExplain(210, "单指单击 2 区域并滑到 5 区域结束,另外一只手指同时触摸压住 3 区域", "\u2816", "二三五点"));
        all_dm_hashMap.put(3045, new DMExplain(211, "单指单击 4 区域并滑到 5 区域结束,另外一只手指同时触摸压住 3 区域", "\u281C", "三四五点"));
        all_dm_hashMap.put(30124, new DMExplain(212, "单指单击 1 区域,滑到 2 区域再转向 4区域结束,另外一只手指同时触摸压住 3 区域", "\u280F", "一二三四点"));
        all_dm_hashMap.put(30125, new DMExplain(213, "单指单击 1 区域,滑到 2 区域再转向 5区域结束,另外一只手指同时触摸压住 3 区域", "\u2817", "一二三五点"));
        all_dm_hashMap.put(30145, new DMExplain(214, "单指单击 1 区域,滑到 4 区域再转向 5区域结束,另外一只手指同时触摸压住 3 区域", "\u281D", "一三四无点"));
        all_dm_hashMap.put(30245, new DMExplain(215, "单指单击 2 区域,滑到 4 区域再转向 5区域结束,另外一只手指同时触摸压住 3 区域", "\u281E", "二三四五点"));
        all_dm_hashMap.put(301245, new DMExplain(216, "单指单击 1 区域,滑到 2 区域,转向 4区域在滑到 5 区域结束,另外一只手指同时触摸压住 3 区域", "\u281F", "缺六方"));
        
        all_dm_hashMap.put(60, new DMExplain(301, "单指触摸 6 区域 2 秒", "\u2820", "六点"));
        all_dm_hashMap.put(601, new DMExplain(302, "单指单击 1 区域,另外一只手指同时触摸压住 6 区域", "\u2821", "一六点"));
        all_dm_hashMap.put(602, new DMExplain(303, "单指单击 2 区域,另外一只手指同时触摸压住 6 区域", "\u2822", "二六点"));
        all_dm_hashMap.put(604, new DMExplain(304, "单指单击 4 区域,另外一只手指同时触摸压住 6 区域", "\u2828", "四六点"));
        all_dm_hashMap.put(605, new DMExplain(305, "单指单击 5 区域,另外一只手指同时触摸压住 6 区域", "\u2830", "五六点"));
        all_dm_hashMap.put(6012, new DMExplain(306, "单指单击 1 区域并滑到 2 区域结束,另外一只手指同时触摸压住 6 区域", "\u2823", "一二六点"));
        all_dm_hashMap.put(6014, new DMExplain(307, "单指单击 1 区域并滑到 4 区域结束,另外一只手指同时触摸压住 6 区域", "\u2829", "一四六点"));
        all_dm_hashMap.put(6015, new DMExplain(308, "单指单击 1 区域并滑到 5 区域结束,另外一只手指同时触摸压住 6 区域", "\u2831", "一五六点"));
        all_dm_hashMap.put(6024, new DMExplain(309, "单指单击 2 区域并滑到 4 区域结束,另外一只手指同时触摸压住 6 区域", "\u282A", "二四六点"));
        all_dm_hashMap.put(6025, new DMExplain(310, "单指单击 2 区域并滑到 5 区域结束,另外一只手指同时触摸压住 6 区域", "\u2832", "二五六点"));
        all_dm_hashMap.put(6045, new DMExplain(311, "单指单击 4 区域并滑到 5 区域结束,另外一只手指同时触摸压住 6 区域", "\u2838", "四五六点"));
        all_dm_hashMap.put(60124, new DMExplain(312, "单指单击 1 区域,滑到 2 区域再转向 4区域结束,另外一只手指同时触摸压住 6 区域", "\u282B", "一二四六点"));
        all_dm_hashMap.put(60125, new DMExplain(313, "单指单击 1 区域,滑到 2 区域再转向 5区域结束,另外一只手指同时触摸压住 6 区域", "\u2833", "一二五六点"));
        all_dm_hashMap.put(60145, new DMExplain(314, "单指单击 1 区域,滑到 4 区域再转向 5区域结束,另外一只手指同时触摸压住 6 区域", "\u2839", "一四五六点"));
        all_dm_hashMap.put(60245, new DMExplain(315, "单指单击 2 区域,滑到 4 区域再转向 5区域结束,另外一只手指同时触摸压住 6 区域", "\u283A", "二四五六点"));
        all_dm_hashMap.put(601245, new DMExplain(316, "单指单击 1 区域,滑到 2 区域,转向 4区域在滑到 5 区域结束,另外一只手指同时触摸压住 6 区域", "\u283B", "缺三方"));
        
        all_dm_hashMap.put(3060, new DMExplain(401, "单指触摸 3,6 区域 2 秒", "\u2824", "三六点"));
        all_dm_hashMap.put(30601, new DMExplain(402, "单指单击 1 区域,另外一只手指同时触摸压住 3,6 区域", "\u2825", "一三六点"));
        all_dm_hashMap.put(30602, new DMExplain(403, "单指单击 2 区域,另外一只手指同时触摸压住 3,6 区域", "\u2826", "二三六点"));
        all_dm_hashMap.put(30604, new DMExplain(404, "单指单击 4 区域,另外一只手指同时触摸压住 3,6 区域", "\u282C", "四三六点"));
        all_dm_hashMap.put(30605, new DMExplain(405, "单指单击 5 区域,另外一只手指同时触摸压住 3,6 区域", "\u2834", "五三六点"));
        all_dm_hashMap.put(306012, new DMExplain(406, "单指单击 1 区域并滑到 2 区域结束,另外一只手指同时触摸压住 3,6 区域", "\u2827", "一二三六点"));
        all_dm_hashMap.put(306014, new DMExplain(407, "单指单击 1 区域并滑到 4 区域结束,另外一只手指同时触摸压住 3,6 区域", "\u282D", "一三四六点"));
        all_dm_hashMap.put(306015, new DMExplain(408, "单指单击 1 区域并滑到 5 区域结束,另外一只手指同时触摸压住 3,6 区域", "\u2835", "一三五六点"));
        all_dm_hashMap.put(306024, new DMExplain(409, "单指单击 2 区域并滑到 4 区域结束,另外一只手指同时触摸压住 3,6 区域", "\u282E", "二三四六点"));
        all_dm_hashMap.put(306025, new DMExplain(410, "单指单击 2 区域并滑到 5 区域结束,另外一只手指同时触摸压住 3,6 区域", "\u2836", "二三五六点"));
        all_dm_hashMap.put(306045, new DMExplain(411, "单指单击 4 区域并滑到 5 区域结束,另外一只手指同时触摸压住 3,6 区域", "\u283C", "三四五六点"));
        all_dm_hashMap.put(3060124, new DMExplain(412, "单指单击 1 区域,滑到 2 区域再转向 4区域结束,另外一只手指同时触摸压住 3,6 区域", "\u282F", "缺五方"));
        all_dm_hashMap.put(3060125, new DMExplain(413, "单指单击 1 区域,滑到 2 区域再转向 5区域结束,另外一只手指同时触摸压住 3,6 区域", "\u2837", "缺四方"));
        all_dm_hashMap.put(3060145, new DMExplain(414, "单指单击 1 区域,滑到 4 区域再转向 5区域结束,另外一只手指同时触摸压住 3,6 区域", "\u283D", "缺二方"));
        all_dm_hashMap.put(3060245, new DMExplain(415, "单指单击 2 区域,滑到 4 区域再转向 5区域结束,另外一只手指同时触摸压住 3,6 区域", "\u283E", "缺一方"));
        all_dm_hashMap.put(30601245, new DMExplain(416, "单指单击 1 区域,滑到 2 区域,转向 4区域在滑到 5 区域结束,另外一只手指同时触摸压住 3,6 区域", "\u283F", "满方"));
    }
    
    private final static String[] base_number_str = new String[]{"一", "二", "三", "四", "五", "六"};
    
    public static DMExplain getDMExplainBySpeak(String speakText) {
        if (Fusion.isEmpty(speakText)) return null;
        StringBuffer stringBuffer = new StringBuffer();
        char[] speak_char = speakText.toCharArray();
        for (char c: speak_char) {
            if (Character.isDigit(c)) {
                try {
                    stringBuffer.append(base_number_str[Integer.parseInt(String.valueOf(c)) - 1]);
                } catch (Exception e) {
                    stringBuffer.append(c);
                }
            } else {
                stringBuffer.append(c);
            }
        }
        String re_speak_py = Cn2Spell.getInstance().getSelling(stringBuffer.toString());
        for (Map.Entry<Integer, DMExplain> entry : all_dm_hashMap.entrySet()) {
            String speak = entry.getValue().getSpeakDes();
            String speak_py = Cn2Spell.getInstance().getSelling(speak);
            if (re_speak_py.equals(speak_py)) {
                return entry.getValue();
            }
        }
        return null;
    }
    
    public static String getDMExplainByUnicode(String content) {
        StringBuffer stringBuffer = new StringBuffer();
        char[] speak_char = content.toCharArray();
        for (char c: speak_char) {
            String isFind = null;
            for (Map.Entry<Integer, DMExplain> entry : all_dm_hashMap.entrySet()) {
                String unicode = entry.getValue().getUnicode();
                if (unicode.equals(String.valueOf(c))) {
                    isFind = entry.getValue().getSpeakDes();
                    break;
                }
            }
            if (!Fusion.isEmpty(isFind)) {
                stringBuffer.append(isFind);
            } else {
                stringBuffer.append(c);
            }
        }
        return stringBuffer.toString();
    }
}
