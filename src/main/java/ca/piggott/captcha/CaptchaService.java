package ca.piggott.captcha;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

public class CaptchaService extends Service<CaptchaConfiguration> {

	public static void main(String... args)
			throws Exception
	{
		new CaptchaService().run(args);
	}

	@Override
	public void initialize(Bootstrap<CaptchaConfiguration> bootstrap) {
		bootstrap.setName("Image Captcha");
		bootstrap.addBundle(new AssetsBundle("/assets", "/", "index.html"));
	}

	@Override
	public void run(CaptchaConfiguration configuration, Environment environment)
			throws Exception
	{
		configuration.getHttpConfiguration().setRootPath("/service/*");

		environment.addResource(new CaptchaResource(getTerms(configuration), configuration.getCredentials(), configuration.getCx()));
	}
	
	private String[] getTerms(CaptchaConfiguration config)
		throws IOException
	{
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new FileReader(new File(config.getTermFile())));
			String line;
			Set<String> lines = new HashSet<String>();
			while ((line = reader.readLine()) != null)
			{
				lines.add(line);
			}
			return lines.toArray(new String[lines.size()]);
		}
		catch (IOException e)
		{
			throw new IllegalStateException(e);
		}
		finally
		{
			if (reader != null)
			{
				reader.close();
			}
		}
	}
}