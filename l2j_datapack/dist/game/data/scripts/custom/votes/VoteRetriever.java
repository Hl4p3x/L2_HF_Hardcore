package custom.votes;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import custom.votes.mappers.VoteEntryMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class VoteRetriever {

    private static final Logger LOG = LoggerFactory.getLogger(VoteRetriever.class);

    public List<VoteEntry> retrieveVotes(String url, L2PcInstance player, VoteEntryMapper mapper) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(url);
            CloseableHttpResponse response = httpClient.execute(get);
            String contents = EntityUtils.toString(response.getEntity());
            return mapper
                    .convert(contents)
                    .stream()
                    .filter(voteEntry -> voteEntry.getCharacterName().toLowerCase().equals(player.getName().toLowerCase()))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            LOG.error("Could not retrieve votes from {}", url, e);
            throw new IllegalStateException("Could not retrieves votes");
        }
    }

}
