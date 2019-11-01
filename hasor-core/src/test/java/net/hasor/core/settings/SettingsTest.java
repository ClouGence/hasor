/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.core.settings;
import net.hasor.core.Settings;
import net.hasor.core.setting.InputStreamSettings;
import net.hasor.core.setting.SettingsWrap;
import net.hasor.core.setting.StandardContextSettings;
import net.hasor.test.core.enums.SelectEnum;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class SettingsTest {
    protected      Logger logger           = LoggerFactory.getLogger(getClass());
    private static String DATA_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static String TIME_FORMAT      = "HH:mm:ss";
    private static String DATA_FORMAT      = "yyyy-MM-dd";

    // - 配置信息读取
    @Test
    public void settingsTest() throws Exception {
        Settings settings = new StandardContextSettings("/net_hasor_core_settings/data-config.xml");
        settings = new SettingsWrap(settings);
        //
        String myName = settings.getString("mySelf.myName");
        assert myName.equals("赵永春");
        //
        Integer myAge = settings.getInteger("mySelf.myAge");
        assert myAge.equals(12);
        //
        Date myBirthday = settings.getDate("mySelf.myBirthday", DATA_TIME_FORMAT);
        assert new SimpleDateFormat(DATA_TIME_FORMAT).parse("1986-01-01 00:00:00").getTime() == myBirthday.getTime();
        //
        String myWork = settings.getString("mySelf.myWork");
        assert myWork.equals("Software Engineer");
        //
        String myProjectURL = settings.getString("mySelf.myProjectURL");
        assert myProjectURL.equals("http://www.hasor.net/");
        //
        String source = settings.getString("mySelf.source");
        assert source.equals("Xml");
    }

    // - 配置信息读取
    @Test
    public void propTest() throws Exception {
        Settings settings = new StandardContextSettings("/net_hasor_core_settings/data-config.properties");
        settings = new SettingsWrap(settings);
        //
        String myName = settings.getString("mySelf.myName");
        assert myName.equals("赵永春");
        //
        Integer myAge = settings.getInteger("mySelf.myAge");
        assert myAge.equals(12);
        //
        Date myBirthday = settings.getDate("mySelf.myBirthday", DATA_TIME_FORMAT);
        assert new SimpleDateFormat(DATA_TIME_FORMAT).parse("1986-01-01 00:00:00").getTime() == myBirthday.getTime();
        //
        String myWork = settings.getString("mySelf.myWork");
        assert myWork.equals("Software Engineer");
        //
        String myProjectURL = settings.getString("mySelf.myProjectURL");
        assert myProjectURL.equals("http://www.hasor.net/");
        //
        String source = settings.getString("mySelf.source");
        assert source.equals("Prop");
    }

    // - 配置信息读取
    @Test
    public void valueTest() throws Exception {
        Settings settings = new StandardContextSettings("/net_hasor_core_settings/value-config.xml");
        settings = new SettingsWrap(settings);
        //
        assert settings.getBoolean("valueGroup.booleanValue_true");
        assert !settings.getBoolean("valueGroup.booleanValue_false", true);
        assert settings.getBoolean("valueGroup.booleanValue_non", true);
        assert settings.getBoolean("valueGroup.booleanValue_yes");
        assert !settings.getBoolean("valueGroup.booleanValue_no");
        assert settings.getBoolean("valueGroup.booleanValue_on");
        assert !settings.getBoolean("valueGroup.booleanValue_off");
        assert settings.getBoolean("valueGroup.booleanValue_y");
        assert !settings.getBoolean("valueGroup.booleanValue_n");
        //
        assert 123 == settings.getShort("valueGroup.shortValue_123");
        assert 456 == settings.getShort("valueGroup.shortValue_456", (short) 456);
        assert 789 == settings.getShort("valueGroup.shortValue_non", (short) 789);
        assert 0 == settings.getShort("valueGroup.shortValue_over");
        //
        assert 123 == settings.getInteger("valueGroup.intValue_123");
        assert 456 == settings.getInteger("valueGroup.intValue_456", 456);
        assert 789 == settings.getInteger("valueGroup.intValue_non", 789);
        assert 0 == settings.getInteger("valueGroup.intValue_over");
        //
        assert 123 == settings.getLong("valueGroup.longValue_123");
        assert 456 == settings.getLong("valueGroup.longValue_456", 456L);
        assert 789 == settings.getLong("valueGroup.longValue_non", 789L);
        assert 0 == settings.getLong("valueGroup.longValue_over");
        //
        assert 2.7182f == settings.getFloat("valueGroup.floatValue_123");
        assert 3.1415f == settings.getFloat("valueGroup.floatValue_456", 3.1415f);
        assert 1.4142f == settings.getFloat("valueGroup.floatValue_non", 1.4142f);
        assert 0 == settings.getLong("valueGroup.floatValue_over");
        //
        assert 2.7182d == settings.getDouble("valueGroup.doubleValue_123");
        assert 3.1415d == settings.getDouble("valueGroup.doubleValue_456", 3.1415d);
        assert 1.4142d == settings.getDouble("valueGroup.doubleValue_non", 1.4142d);
        assert 12345678901234567891310123456789012345678913101234567890123456789131012345678901234567891310.12345678901234567891310123456789012345678913101234567890123456789131012345678901234567891310d == settings.getDouble("valueGroup.doubleValue_lage");
        assert 1.23e10 == settings.getDouble("valueGroup.doubleValue_str");
        //
        assert "abc".equals(settings.getString("valueGroup.stringValue"));
        assert 'a' == settings.getChar("valueGroup.charValue");
        assert 'a' == settings.getChar("valueGroup.charValue_over");
        //
        Date df1 = settings.getDate("valueGroup.dateValue_3", TIME_FORMAT);
        assert new SimpleDateFormat(TIME_FORMAT).parse("00:00:00").getTime() == df1.getTime();
        Date df2 = settings.getDate("valueGroup.dateValue_2", DATA_FORMAT);
        assert new SimpleDateFormat(DATA_FORMAT).parse("1986-01-01").getTime() == df2.getTime();
        Date df3 = settings.getDate("valueGroup.dateValue_non", TIME_FORMAT, 12345);
        assert df3.getTime() == 12345;
        Date df4 = settings.getDate("valueGroup.dateValue_non", TIME_FORMAT, new Date(12345));
        assert df4.getTime() == 12345;
        Date df5 = settings.getDate("valueGroup.dateValue_1", DATA_TIME_FORMAT);
        assert new SimpleDateFormat(DATA_TIME_FORMAT).parse("1986-01-01 00:00:00").getTime() == df5.getTime();
        //
        Date d1 = settings.getDate("valueGroup.dateValue_6");
        assert new SimpleDateFormat(TIME_FORMAT).parse("00:00:00").getTime() == d1.getTime();
        Date d2 = settings.getDate("valueGroup.dateValue_5");
        assert new SimpleDateFormat(DATA_FORMAT).parse("1986-01-01").getTime() == d2.getTime();
        Date d3 = settings.getDate("valueGroup.dateValue_non", 12345);
        assert d3.getTime() == 12345;
        Date d4 = settings.getDate("valueGroup.dateValue_non", new Date(12345));
        assert d4.getTime() == 12345;
        Date d5 = settings.getDate("valueGroup.dateValue_4");
        assert new SimpleDateFormat(DATA_TIME_FORMAT).parse("1986-01-01 00:00:00").getTime() == d5.getTime();
        //
        assert SelectEnum.One == settings.getEnum("valueGroup.enumValue_1", SelectEnum.class);
        assert SelectEnum.One == settings.getEnum("valueGroup.enumValue_2", SelectEnum.class);
        assert SelectEnum.Two == settings.getEnum("valueGroup.enumValue_3", SelectEnum.class);
        assert SelectEnum.Three == settings.getEnum("valueGroup.enumValue_4", SelectEnum.class, SelectEnum.Three);
        //
        assert "c:\\user\\abc.txt".equals(settings.getFilePath("valueGroup.fileValue_1"));
        assert settings.getFilePath("valueGroup.fileValue_2") == null;
        assert "/root/user/abc.txt".equals(settings.getFilePath("valueGroup.fileValue_3"));
        assert "/root/user".equals(settings.getFilePath("valueGroup.fileValue_4"));
        assert settings.getFilePath("valueGroup.fileValue_5") == null;
        assert "abc".equals(settings.getFilePath("valueGroup.fileValue_2", "abc"));
        assert "abc".equals(settings.getFilePath("valueGroup.fileValue_5", "abc"));
        //
        assert "c:\\user\\".equals(settings.getDirectoryPath("valueGroup.fileValue_1"));
        assert "c:\\".equals(settings.getDirectoryPath("valueGroup.fileValue_2"));
        assert "/root/user/".equals(settings.getDirectoryPath("valueGroup.fileValue_3"));
        assert "/root/".equals(settings.getDirectoryPath("valueGroup.fileValue_4"));
        assert "/".equals(settings.getDirectoryPath("valueGroup.fileValue_5"));
        assert "abc".equals(settings.getDirectoryPath("valueGroup.fileValue_6", "abc"));
    }

    // - 配置信息读取
    @Test
    public void valueTest2() throws Exception {
        Settings settings = new StandardContextSettings("/net_hasor_core_settings/value-config.xml");
        settings = new SettingsWrap(settings);
        //
        assert settings.getBooleanArray("valueGroup.booleanValue_true")[0];
        assert !settings.getBooleanArray("valueGroup.booleanValue_false", true)[0];
        assert settings.getBooleanArray("valueGroup.booleanValue_non").length == 0;
        assert settings.getBooleanArray("valueGroup.booleanValue_non", true).length == 0;
        assert settings.getBooleanArray("valueGroup.booleanValue_yes")[0];
        assert !settings.getBooleanArray("valueGroup.booleanValue_no")[0];
        assert settings.getBooleanArray("valueGroup.booleanValue_on")[0];
        assert !settings.getBooleanArray("valueGroup.booleanValue_off")[0];
        assert settings.getBooleanArray("valueGroup.booleanValue_y")[0];
        assert !settings.getBooleanArray("valueGroup.booleanValue_n")[0];
        //
        assert 123 == settings.getShortArray("valueGroup.shortValue_123")[0];
        assert 456 == settings.getShortArray("valueGroup.shortValue_456", (short) 456)[0];
        assert settings.getShortArray("valueGroup.shortValue_non").length == 0;
        assert settings.getShortArray("valueGroup.shortValue_non", (short) 789).length == 0;
        assert 0 == settings.getShortArray("valueGroup.shortValue_over")[0];
        //
        assert 123 == settings.getIntegerArray("valueGroup.intValue_123")[0];
        assert 456 == settings.getIntegerArray("valueGroup.intValue_456", 456)[0];
        assert settings.getIntegerArray("valueGroup.intValue_non").length == 0;
        assert settings.getIntegerArray("valueGroup.intValue_non", 789).length == 0;
        assert 0 == settings.getIntegerArray("valueGroup.intValue_over")[0];
        //
        assert 123 == settings.getLongArray("valueGroup.longValue_123")[0];
        assert 456 == settings.getLongArray("valueGroup.longValue_456", 456L)[0];
        assert settings.getLongArray("valueGroup.longValue_non").length == 0;
        assert settings.getLongArray("valueGroup.longValue_non", 789L).length == 0;
        assert 0 == settings.getLongArray("valueGroup.longValue_over")[0];
        //
        assert 2.7182f == settings.getFloatArray("valueGroup.floatValue_123")[0];
        assert 3.1415f == settings.getFloatArray("valueGroup.floatValue_456", 3.1415f)[0];
        assert settings.getFloatArray("valueGroup.floatValue_non").length == 0;
        assert settings.getFloatArray("valueGroup.floatValue_non", 1.4142f).length == 0;
        assert 0 == settings.getLongArray("valueGroup.floatValue_over")[0];
        //
        assert 2.7182d == settings.getDoubleArray("valueGroup.doubleValue_123")[0];
        assert 3.1415d == settings.getDoubleArray("valueGroup.doubleValue_456", 3.1415d)[0];
        assert settings.getDoubleArray("valueGroup.doubleValue_non").length == 0;
        assert settings.getDoubleArray("valueGroup.doubleValue_non", 1.4142d).length == 0;
        assert 12345678901234567891310123456789012345678913101234567890123456789131012345678901234567891310.12345678901234567891310123456789012345678913101234567890123456789131012345678901234567891310d == settings.getDoubleArray("valueGroup.doubleValue_lage")[0];
        assert 1.23e10 == settings.getDoubleArray("valueGroup.doubleValue_str")[0];
        //
        assert "abc".equals(settings.getStringArray("valueGroup.stringValue")[0]);
        assert 'a' == settings.getCharArray("valueGroup.charValue")[0];
        assert 'a' == settings.getCharArray("valueGroup.charValue_over")[0];
        //
        Date df1 = settings.getDateArray("valueGroup.dateValue_3", TIME_FORMAT)[0];
        assert new SimpleDateFormat(TIME_FORMAT).parse("00:00:00").getTime() == df1.getTime();
        Date df2 = settings.getDateArray("valueGroup.dateValue_2", DATA_FORMAT)[0];
        assert new SimpleDateFormat(DATA_FORMAT).parse("1986-01-01").getTime() == df2.getTime();
        assert settings.getDateArray("valueGroup.dateValue_non", TIME_FORMAT, 12345).length == 0;
        assert settings.getDateArray("valueGroup.dateValue_non", TIME_FORMAT, new Date(12345)).length == 0;
        //
        Date d1 = settings.getDateArray("valueGroup.dateValue_6")[0];
        assert new SimpleDateFormat(TIME_FORMAT).parse("00:00:00").getTime() == d1.getTime();
        Date d2 = settings.getDateArray("valueGroup.dateValue_5")[0];
        assert new SimpleDateFormat(DATA_FORMAT).parse("1986-01-01").getTime() == d2.getTime();
        assert settings.getDateArray("valueGroup.dateValue_non", 12345).length == 0;
        assert settings.getDateArray("valueGroup.dateValue_non", new Date(12345)).length == 0;
        //
        assert SelectEnum.One == settings.getEnumArray("valueGroup.enumValue_1", SelectEnum.class)[0];
        assert SelectEnum.One == settings.getEnumArray("valueGroup.enumValue_2", SelectEnum.class)[0];
        assert SelectEnum.Two == settings.getEnumArray("valueGroup.enumValue_3", SelectEnum.class)[0];
        assert settings.getEnumArray("valueGroup.enumValue_4", SelectEnum.class, SelectEnum.Three).length == 0;
        //
        assert "c:\\user\\abc.txt".equals(settings.getFilePathArray("valueGroup.fileValue_1")[0]);
        assert settings.getFilePathArray("valueGroup.fileValue_2").length == 0;
        assert "/root/user/abc.txt".equals(settings.getFilePathArray("valueGroup.fileValue_3")[0]);
        assert "/root/user".equals(settings.getFilePathArray("valueGroup.fileValue_4")[0]);
        assert settings.getFilePathArray("valueGroup.fileValue_5").length == 0;
        assert settings.getFilePathArray("valueGroup.fileValue_2", "abc").length == 0;
        assert settings.getFilePathArray("valueGroup.fileValue_5", "abc").length == 0;
        //
        assert "c:\\user\\".equals(settings.getDirectoryPathArray("valueGroup.fileValue_1")[0]);
        assert "c:\\".equals(settings.getDirectoryPathArray("valueGroup.fileValue_2")[0]);
        assert "/root/user/".equals(settings.getDirectoryPathArray("valueGroup.fileValue_3")[0]);
        assert "/root/".equals(settings.getDirectoryPathArray("valueGroup.fileValue_4")[0]);
        assert "/".equals(settings.getDirectoryPathArray("valueGroup.fileValue_5")[0]);
        assert settings.getDirectoryPathArray("valueGroup.fileValue_6", "abc").length == 0;
    }

    @Test
    public void valueTest3() throws Exception {
        Settings settings = new StandardContextSettings("/net_hasor_core_settings/value-config.xml");
        settings = new SettingsWrap(settings);
        //
        assert settings.getBoolean("valueGroup.booleanValue_true");
        settings.setSetting("valueGroup.booleanValue_true", false);
        assert !settings.getBoolean("valueGroup.booleanValue_true");
        settings.setSetting("valueGroup.booleanValue_true", "n");
        assert !settings.getBoolean("valueGroup.booleanValue_true");
        //
        settings.addSetting("valueGroup.booleanValue_true", true, "http://schema_a");
        Boolean[] array = settings.getBooleanArray("valueGroup.booleanValue_true");
        assert array.length == 2;
        assert !array[0];
        assert array[1];
    }

    @Test
    public void valueTest4() throws Exception {
        Settings settings = new InputStreamSettings();
        //
        settings.addSetting("charValue", 32);
        assert settings.getChar("charValue") == '3';// 32 会被toString
        //
        settings.addSetting("charValue", ' ');
        assert settings.getChar("charValue") == ' ';
    }
    //
    //    @Test
    //    public void valueTest4() throws Exception {
    //        StandardContextSettings settings = new StandardContextSettings("/net_hasor_core_settings/value-config.xml");
    //        //
    //        assert "%JAVA_HOME%/bin/javac.exe".equals(settings.getString("valueGroup.evalValue"));
    //        settings.resetValues(new UpdateValue() {
    //            @Override
    //            public void update(SettingValue oldValue, Settings context) {
    //                Object defaultVar = oldValue.getDefaultVar();
    //                if (defaultVar instanceof XmlNode) {
    //                    if (((XmlNode) defaultVar).getName().equals("valueGroup.evalValue")) {
    //                        oldValue.setDefaultVar("/root/java8/bin/javac.exe");
    //                    }
    //                }
    //            }
    //        });
    //        assert "/root/java8/bin/javac.exe".equals(settings.getString("valueGroup.evalValue"));
    //    }
}