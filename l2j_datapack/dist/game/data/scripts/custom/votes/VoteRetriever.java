package custom.votes;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import custom.votes.mappers.VoteEntryMapper;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

public class VoteRetriever {

    private static final Logger LOG = LoggerFactory.getLogger(VoteRetriever.class);

    public List<VoteEntry> retrieveVotes(String url, L2PcInstance player, VoteEntryMapper mapper) {
        try (CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).build()) {
            HttpGet get = new HttpGet(url.replaceAll("%IP_ADDRESS%", player.getIPAddress()));
            CloseableHttpResponse response = httpClient.execute(get);
            String contents = EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8"));
            return mapper
                    .convert(player, contents)
                    .stream()
                    .filter(voteEntry -> voteEntry.getCharacterName().toLowerCase().equals(player.getName().toLowerCase()))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            LOG.error("Could not retrieve votes from {} because of: {}", mapper.getSourceCode(), e.getMessage());
            throw new IllegalStateException("Could not retrieves votes");
        }
    }

}
