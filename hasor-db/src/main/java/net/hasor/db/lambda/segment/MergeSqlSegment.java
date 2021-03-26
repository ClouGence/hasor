/*
 * Copyright 2002-2005 the original author or authors.
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
package net.hasor.db.lambda.segment;
import net.hasor.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 提供多个 Segment 汇聚成为一个的工具。
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public final class MergeSqlSegment implements Segment {
    private final List<Segment> segments = new ArrayList<>();

    public MergeSqlSegment(List<Segment> segments) {
        this.segments.addAll(segments);
    }

    public MergeSqlSegment(Segment... segments) {
        this.segments.addAll(Arrays.asList(segments));
    }

    public void addSegment(Segment... segmentArrays) {
        if (segmentArrays != null) {
            this.segments.addAll(Arrays.asList(segmentArrays));
        }
    }

    @Override
    public String getSqlSegment() {
        return this.getSqlSegment(this.segments);
    }

    public MergeSqlSegment sub(int form) {
        return new MergeSqlSegment(this.segments.subList(form, this.segments.size()));
    }

    public String noFirstSqlSegment() {
        return this.getSqlSegment(this.segments.subList(1, this.segments.size()));
    }

    private String getSqlSegment(List<Segment> dataList) {
        StringBuilder strBuilder = new StringBuilder("");
        for (Segment segment : dataList) {
            String str = segment.getSqlSegment();
            if (StringUtils.isNotBlank(str)) {
                strBuilder.append(str).append(" ");
            } else {
                strBuilder.append(" ");
            }
        }
        return strBuilder.toString().trim();
    }

    public boolean isEmpty() {
        return this.segments.isEmpty();
    }

    public Segment firstSqlSegment() {
        if (this.segments.isEmpty()) {
            return null;
        } else {
            Segment segment = segments.get(0);
            if (segment instanceof MergeSqlSegment) {
                return ((MergeSqlSegment) segment).firstSqlSegment();
            } else {
                return segment;
            }
        }
    }
}
