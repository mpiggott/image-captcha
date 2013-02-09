package ca.piggott.captcha;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.codehaus.plexus.util.IOUtil;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.customsearch.Customsearch;
import com.google.api.services.customsearch.Customsearch.Cse;
import com.google.api.services.customsearch.model.Result;
import com.google.api.services.customsearch.model.Search;
import com.google.common.cache.CacheLoader;

public class CaptchaLoader extends CacheLoader<String, Captcha> {
	private final String[] terms;

	private ApacheHttpTransport transport = new ApacheHttpTransport();
	
	private JsonFactory factory = new JacksonFactory();
	
	private Customsearch search;
	
	private String key;

	private String cx;
	
	public CaptchaLoader(String[] terms, String key, String cx)
	{
		search = new Customsearch(transport, factory, new HttpInitializer());
		this.terms = terms;
		this.key = key;
		this.cx = cx;
	}

	@Override
	public Captcha load(String key)
			throws Exception
	{
		final String term = terms[(int)Math.floor(Math.random() * terms.length)];
		Cse.List list = search.cse().list(term);
		list.setImgSize("medium");
		list.setImgType("photo");
		list.setSearchType("image");
		list.setKey(this.key);
		list.setCx(cx);
//		list.setRights("cc_publicdomain");
		return getResult(list.execute(), term);
	}

	private Captcha getResult(Search search, String term)
			throws ClientProtocolException, IOException
	{
		if (search.getItems().isEmpty())
		{
			throw new IllegalStateException("Search Result is Empty");
		}
		Captcha img;
		do
		{
			Result result = search.getItems().get((int)(search.getItems().size() * Math.random()));
			search.getItems().remove(result);
			String url = result.getImage().getThumbnailLink();
			img = getImage(url, term);
		} while (img == null && !search.getItems().isEmpty());

		return img;
	}

	private Captcha getImage(String url, String term)
			throws ClientProtocolException, IOException
	{
		HttpGet get = new HttpGet(url);
		HttpResponse resp = transport.getHttpClient().execute(get);

		if (resp.getStatusLine().getStatusCode() != 200)
		{
			return null;
		}
		byte[] bytes;

		InputStream in = null;
		try
		{
			in = resp.getEntity().getContent();
			bytes = IOUtil.toByteArray(in);
		}
		finally
		{
			IOUtil.close(in);
		}
		return new Captcha(bytes, term, resp.getEntity().getContentType().getValue());
	}

	private static class HttpInitializer implements HttpRequestInitializer
	{

		public void initialize(HttpRequest request)
				throws IOException
		{
		}
	}
}
