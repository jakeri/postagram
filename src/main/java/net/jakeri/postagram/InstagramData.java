package net.jakeri.postagram;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class InstagramData {

	private final String accessToken;
	private final ObjectMapper mapper = new ObjectMapper();
	private static final String RECENT_URL = "https://api.instagram.com/v1/users/%s/media/recent/?access_token=%s&count=%d";
	private static final String SEARCH_URL = "https://api.instagram.com/v1/users/search?access_token=%s&count=1&q=%s";

	public InstagramData(String accessToken) {
		super();
		this.accessToken = accessToken;
	}

	private String getUserId(String userName) {

		try {
			String url = String.format(SEARCH_URL, this.accessToken, userName);
			JsonNode actualObj = queryJson(url);

			JsonNode node = actualObj.get("data").get(0);
			if (node != null) {
				return node.get("id").asText();
			} else {
				throw new RuntimeException("No user found with name: " + userName);
			}

		} catch (JsonParseException e) {
			throw new RuntimeException(e);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public List<Image> fetchImages(final String userName, final int imagesNeeded) {

		String userId = getUserId(userName);

		List<Image> images = new ArrayList<Image>();
		fetchImages(images, userId, imagesNeeded, null);

		if (images.size() < imagesNeeded) {
			System.out
					.println(String
							.format("Needed %d images but found only %d images in instagram feed.",
									imagesNeeded, images.size()));
		}
		return images;
	}

	private void fetchImages(final List<Image> images, final String userId,
			final int imagesNeeded, String maxId) {

		// If more than 40 then paginate query.

		int imagesLeftToFetch = imagesNeeded - images.size();

		try {
			String url = String.format(RECENT_URL, userId, this.accessToken,
					imagesLeftToFetch);
			if (maxId != null) {
				url = url + "&max_id=" + maxId;
			}
			JsonNode actualObj = queryJson(url);

			String lastId = null;
			for (JsonNode jsonNode : actualObj.get("data")) {

				JsonNode standardImg = jsonNode.get("images").get(
						"standard_resolution");
				String urlImg = standardImg.get("url").textValue();
				int width = standardImg.get("width").intValue();
				int height = standardImg.get("height").intValue();

				lastId = jsonNode.get("id").asText();
				images.add(new Image(urlImg, width, height));

			}
			if (images.size() < imagesNeeded && lastId != null) {
				fetchImages(images, userId, imagesNeeded, lastId);
			}

		} catch (JsonParseException e) {
			throw new RuntimeException(e);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private JsonNode queryJson(String url) throws MalformedURLException,
			IOException, JsonParseException, JsonProcessingException {
		URL urlObj = new URL(url);
		System.out.println(urlObj.toString());
		JsonFactory factory = mapper.getFactory();
		JsonParser jp = factory.createJsonParser(urlObj);
		JsonNode actualObj = mapper.readTree(jp);
		return actualObj;
	}

}
