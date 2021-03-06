package com.fun.frame;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.fun.base.bean.AbstractBean;
import com.fun.config.Constant;
import com.fun.utils.Emoji;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings("all")
public class Output extends Constant {

    private static Logger logger = LoggerFactory.getLogger(Output.class);

    private static final String UP = "~☢~~☢~~☢~~☢~~☢~~☢~~☢~~☢~~☢~~☢~";

    private static final String DOWN = "~☢~~☢~~☢~~☢~~☢~~☢~~☢~~☢~~☢~~☢~";

    public static void output(AbstractBean bean) {
        output(bean.toJson());
    }

    /**
     * 输出，自带log方法,排除root用户使用输出
     *
     * @param text
     */
    public static void output(String text) {
        logger.info(text);
    }

    /**
     * 输出，针对各种不同情况做兼容
     * <p>
     * 在处理两个对象，默认情况第一个是说明文字，第二个是list内容
     * </p>
     *
     * @param object
     */
    public static void output(Object... object) {
        if (ArrayUtils.isEmpty(object)) {
            logger.warn("怎么空了呢！");
        } else if (object.length == 1) {
            if (object[0] instanceof List) {
                output((List) object[0]);
            } else {
                output(object[0].toString());
            }
        } else if (object.length == 2) {
            output(object[0]);
            if (object[1] instanceof List) {
                output((List) object[1]);
            } else {
                output(object[1]);
            }
        } else if (object.getClass().isArray()) {
            output(Arrays.asList(object));
        }
    }

    public static void output(List list) {
        list.forEach(x -> output("第" + (list.indexOf(x) + 1) + "个：" + x.toString()));
    }

    public static void output(Iterator its) {
        its.forEachRemaining(x -> output(x.toString()));
    }

    public static void output(Map map) {
        if (map == null || map.size() == 0) {
            logger.warn("怎么空了呢！");
        } else {
            show(map);
        }
    }

    /**
     * 输出json数组
     *
     * @param jsonArray
     */
    public static void output(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.isEmpty()) {
            output("jsonarray对象为空!");
            return;
        }
        jsonArray.forEach(x -> {
            try {
                output(JSON.parseObject(x.toString()));
            } catch (JSONException e) {
                output(x);
            }
        });
    }

    /**
     * 输出数组
     *
     * @param arrays
     */
    public static <T extends Number> void output(T[] nums) {
        if (ArrayUtils.isEmpty(nums)) return;
        Arrays.asList(nums).forEach(x -> output(x));
    }

    /**
     * 泛型做输出数字对象
     *
     * @param x
     * @param <T>
     */
    public static <T extends Number> void output(T x) {
        output(x.toString());
    }

    public static void output(Object o) {
        if (o == null) logger.warn("怎么空了呢！");
        else output(o.toString());
    }

    public static void output(int[] nums) {
        output(ArrayUtils.toObject(nums));
    }

    public static void output(long[] nums) {
        output(ArrayUtils.toObject(nums));
    }

    public static void output(double[] nums) {
        output(ArrayUtils.toObject(nums));
    }

    /**
     * 输出json
     *
     * @param jsonObject json格式响应实体
     */
    public static JSONObject output(JSONObject jsonObject) {
        if (jsonObject == null || jsonObject.size() == 0) {
            output("json 对象是空的！");
            return jsonObject;
        }
        String jsonStr = jsonObject.toString();// 先将json对象转化为string对象
        jsonStr = jsonStr.replaceAll("\\\\/", OR);
        int level = 0;// 用户标记层级
        StringBuffer jsonResultStr = new StringBuffer("＞  ");// 新建stringbuffer对象，用户接收转化好的string字符串
        int length = jsonStr.length();
        for (int i = 0; i < length; i++) {// 循环遍历每一个字符
            char piece = jsonStr.charAt(i);// 获取当前字符
            // 如果上一个字符是断行，则在本行开始按照level数值添加标记符，排除第一行
            if ('\n' == jsonResultStr.charAt(jsonResultStr.length() - 1)) {
                jsonResultStr.append(Emoji.getSerialEmoji(level) + " . ");
                IntStream.range(0, level - 1).forEach(x -> jsonResultStr.append(". . "));//没有采用sourcecode的getmanystring
            }
            char last = i == 0 ? '{' : jsonStr.charAt(i - 1);
            char next = i < length - 1 ? jsonStr.charAt(i + 1) : '}';
            switch (piece) {
                case ',':
                    // 如果是“,”，则断行
                    jsonResultStr.append(piece + (("\"0123456789le]}".contains(last + EMPTY) && "\"[{".contains(next + EMPTY)) ? LINE : EMPTY));
                    break;
                case '{':
                case '[':
                    // 如果字符是{或者[，则断行，level加1
                    jsonResultStr.append(piece + (":[{,".contains(last + EMPTY) && ",[{}]\"0123456789le".contains(next + EMPTY) ? LINE : EMPTY));
                    if (last != '[') level++;//解决jsonarray:[{
                    break;
                case '}':
                case ']':
                    // 如果是}或者]，则断行，level减1
//                    jsonResultStr.append(LINE);
                    jsonResultStr.append(("\"0123456789le]}{[,".contains(last + EMPTY) && "}],".contains(next + EMPTY) ? LINE : EMPTY));
                    if (next != ']') level--;//解决jsonarray:[{
                    jsonResultStr.append(level == 0 ? "" : Emoji.getSerialEmoji(level) + " . ");
                    IntStream.range(0, level - 1).forEach(x -> jsonResultStr.append(". . "));//没有采用sourcecode的getmanystring
                    jsonResultStr.append(piece);
                    break;
                default:
                    jsonResultStr.append(piece);
                    break;
            }
        }
        output(LINE + UP + " JSON " + UP + LINE + jsonResultStr.toString().replaceAll(LINE, LINE + "＞  ") + LINE + DOWN + " JSON " + DOWN);
        return jsonObject;
    }

    /**
     * 格式化输出内容
     *
     * @param format
     * @param o
     */
    public static void output(String format, Object... o) {
        System.out.printf(format, o);
    }

    public static void show(Map map) {
        new ConsoleTable(map);
    }

    public static void show(List<List<String>> rows) {
        new ConsoleTable(rows);
    }

    /**
     * 打印可能的json数据
     *
     * @param content
     */
    public static void showStr(String content) {
        try {
            if (content.contains("&")) output(SourceCode.getJson(content.split("&")));
            else output(JSONObject.parseObject(content));
        } catch (JSONException e) {
            output(content);
        }
    }

    static class ConsoleTable extends SourceCode {

        List<Integer> rowLength = new ArrayList<>();

        public static void show(Map map) {
            new ConsoleTable(map);
        }

        /**
         * 输出map
         *
         * @param map
         */
        private ConsoleTable(Map map) {
            Set set = map.keySet();
            int asInt0 = set.stream().mapToInt(key -> key.toString().length()).max().getAsInt();
            rowLength.add(asInt0 + 2);
            List<String> values = new ArrayList<>();
            set.forEach(key -> values.add(map.get(key).toString()));
            int asInt1 = values.stream().mapToInt(value -> value.length()).max().getAsInt();
            rowLength.add(asInt1 + 2);
            StringBuffer stringBuffer = new StringBuffer(LINE + getHeader());
            map.forEach((k, v) -> {
                stringBuffer.append(getCel(0, k.toString()));
                stringBuffer.append(getCel(1, v.toString()));
            });
            output(stringBuffer.append(LINE + getHeader()).toString());
        }

        /**
         * 输出list
         *
         * @param rows
         */
        private ConsoleTable(List<List<String>> rows) {
            for (int i = 0; i < rows.size(); i++) {
                List<String> line = rows.get(i);
                for (int j = 0; j < line.size(); j++) {
                    String s = line.get(j);
                    if (rowLength.size() <= j) rowLength.add(0);
                    if (rowLength.get(j) < s.length()) rowLength.set(j, s.length());
                }
            }
            rowLength = rowLength.stream().map(n -> n + 2).collect(Collectors.toList());
            StringBuffer stringBuffer = new StringBuffer(LINE + getHeader());
            for (int i = 0; i < rows.size(); i++) {
                List<String> line = rows.get(i);
                for (int j = 0; j < rowLength.size(); j++) {
                    stringBuffer.append(getCel(j, j < line.size() ? line.get(j) : EMPTY));
                }
            }
            output(stringBuffer.append(LINE + getHeader()).toString());
        }


        /**
         * 获取每一格的string
         *
         * @param colum   列
         * @param content 格内容
         * @return
         */
        public String getCel(int colum, String content) {
            Integer integer = rowLength.get(colum);
            int i = integer - content.length();
            return (colum == 0 ? LINE + PART : PART) + getManyString(SPACE_1, i / 2) + content + getManyString(SPACE_1, i - i / 2) + (rowLength.size() - colum == 1 ? PART : EMPTY);
        }

        /**
         * 获取头尾行
         *
         * @return
         */
        private String getHeader() {
            List<String> collect = rowLength.stream().map(size -> getManyString("-", size)).collect(Collectors.toList());
            return "+" + StringUtils.join(collect.toArray(), "+") + "+";
        }


    }


}
