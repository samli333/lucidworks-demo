package com.ferguson.feedengine.batch.step.stibofeed.convertor;

import com.ferguson.feedengine.batch.utils.XMLStreamParser;
import com.ferguson.feedengine.data.model.AssetBean;
import com.ferguson.feedengine.data.model.ESBean;
import com.ferguson.feedengine.data.model.ValueBean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component("AssetConvertor")
public class AssetConvertor implements CatalogDataCovertor {

    @Override
    public ESBean convert(Map input) {
        if (input == null) {
            return null;
        }
        AssetBean asset = new AssetBean();
        asset.setId((String) input.remove("ID"));
        asset.setName((String) input.remove("Name"));

        Map values = (Map) input.remove("Values");
        List<Map> vals = (List<Map>) values.get("Values");
        vals.stream().forEach(
                e -> {
                    ValueBean bean = new ValueBean();
                    bean.setId((String) e.get("ID"));
                    bean.setAttributeID((String) e.get("AttributeID"));
                    bean.setUnitID((String) e.get("UnitID"));
                    bean.setValue((String) e.get("_value_"));
                    bean.setChanged(Boolean.valueOf((String) e.get("Changed")));
                    asset.getValues().add(bean);
                }
        );

        input.remove(XMLStreamParser.ELEMENT_NAME);
        asset.getOtherProperties().putAll(input);
        return asset;
    }


}
