package com.piotics.common.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.piotics.model.ActivityMarker;
import com.piotics.model.Post;

public class CustomDeserializer extends JsonDeserializer<ActivityMarker> {

	private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.addMixIn(Post.class, CustomDeserializer.class);
    }


	@Override
	public ActivityMarker deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		ObjectMapper mapper = (ObjectMapper) jp.getCodec();
		ObjectNode root = mapper.readTree(jp);
		
		/* write your own condition */
//	        if (root.has("name") && root.get("name").asText().equals("XYZ")) {
//	            return mapper.readValue(root.toString(), ServiceUser.class);
//	        }
		return mapper.readValue(root.toString(), Post.class);
	}

}