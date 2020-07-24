package com.apicatalog.alps.jsonp;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import javax.json.JsonObject;
import javax.json.JsonValue;

import com.apicatalog.alps.AlpsParserException;
import com.apicatalog.alps.dom.element.AlpsLink;

class AlpsJsonLink implements AlpsLink {

    private URI href;
    private String rel;
    
    @Override
    public URI getHref() {
        return href;
    }

    @Override
    public String getRel() {
        return rel;
    }

    public static final Set<AlpsLink> parse(final JsonValue value) throws AlpsParserException {
        
        final Set<AlpsLink> links = new HashSet<>();
        
        for (final JsonValue item : JsonUtils.toArray(value)) {
            
            if (JsonUtils.isNotObject(item)) {
                throw new AlpsParserException("Link property must be JSON object but was " + item.getValueType());
            }
            
            links.add(parseObject(item.asJsonObject()));
        }
        
        return links;
    }
    
    private static final AlpsLink parseObject(final JsonObject linkObject) throws AlpsParserException {
        
        final AlpsJsonLink link = new AlpsJsonLink();
        
        if (!linkObject.containsKey(AlpsJsonConstant.HREF_KEY)) {
            throw new AlpsParserException("Link object must contain 'href' property");
        }

        if (!linkObject.containsKey(AlpsJsonConstant.REL_KEY)) {
            throw new AlpsParserException("Link object must contain 'rel' property");
        }
        
        final JsonValue href = linkObject.get(AlpsJsonConstant.HREF_KEY);
        
        if (JsonUtils.isNotString(href)) {
            throw new AlpsParserException("Link.href property must be URI but was " + href.getValueType());
        }
        
        try {
            
            link.href = URI.create(JsonUtils.getString(href));
            
        } catch (IllegalArgumentException e) {
            throw new AlpsParserException("Link.href property must be URI but was " + href);
        }

        final JsonValue rel = linkObject.get(AlpsJsonConstant.REL_KEY);
        
        if (JsonUtils.isNotString(href)) {
            throw new AlpsParserException("Link.rel property must be string but was " + rel.getValueType());
        }

        link.rel = JsonUtils.getString(rel);
        
        return link;
    }
    
}
