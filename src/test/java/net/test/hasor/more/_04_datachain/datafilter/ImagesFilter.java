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
package net.test.hasor.more._04_datachain.datafilter;
import java.util.ArrayList;
import java.util.List;
import org.more.bizcommon.datachain.DataFilter;
import org.more.bizcommon.datachain.DataFilterChain;
import org.more.bizcommon.datachain.Domain;
import net.hasor.core.Inject;
import net.test.hasor.more._04_datachain.dao.ImageDao;
import net.test.hasor.more._04_datachain.domain.dto.NewsContentDO;
import net.test.hasor.more._04_datachain.domain.dto.NewsImagesDO;
import net.test.hasor.more._04_datachain.domain.vo.NewsContentVO;
import net.test.hasor.more._04_datachain.domain.vo.NewsImagesVO;
/**
 * 负责例子中，新闻图片转换。
 * @version : 2016年5月7日
 * @author 赵永春(zyc@hasor.net)
 */
public class ImagesFilter implements DataFilter<NewsContentDO, NewsContentVO> {
    @Inject
    private ImageDao imageDao;
    @Override
    public NewsContentVO doForward(Domain<NewsContentDO> domain, DataFilterChain<NewsContentDO, NewsContentVO> chain) throws Throwable {
        NewsContentDO dto = domain.getDomain();
        NewsContentVO vo = chain.doForward(domain);
        //
        List<NewsImagesDO> images = this.imageDao.queryImagesByNewsId(dto.getId());
        if (images == null || images.isEmpty()) {
            return vo;
        }
        //
        List<NewsImagesVO> imageVoList = new ArrayList<NewsImagesVO>();
        for (NewsImagesDO image : images) {
            NewsImagesVO imageVo = new NewsImagesVO();
            imageVo.setDesc(image.getDesc());
            imageVo.setId(image.getId());
            imageVo.setImgUrl(image.getImgUrl());
            imageVoList.add(imageVo);
        }
        vo.setImageList(imageVoList);
        return vo;
    }
    @Override
    public NewsContentDO doBackward(Domain<NewsContentVO> domain, DataFilterChain<NewsContentDO, NewsContentVO> chain) throws Throwable {
        NewsContentVO vo = domain.getDomain();
        NewsContentDO dto = chain.doBackward(domain);
        //
        List<NewsImagesVO> imageVoList = vo.getImageList();
        if (imageVoList == null || imageVoList.isEmpty()) {
            return dto;
        }
        //
        List<NewsImagesDO> images = new ArrayList<NewsImagesDO>();
        for (NewsImagesVO imageVo : imageVoList) {
            NewsImagesDO imageDo = new NewsImagesDO();
            imageVo.setDesc(imageVo.getDesc());
            imageVo.setId(imageVo.getId());
            imageVo.setImgUrl(imageVo.getImgUrl());
            images.add(imageDo);
        }
        dto.setImages(images);
        return dto;
    }
}