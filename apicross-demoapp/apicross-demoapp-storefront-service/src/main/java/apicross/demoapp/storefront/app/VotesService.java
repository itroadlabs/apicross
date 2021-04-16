package apicross.demoapp.storefront.app;

import apicross.demoapp.storefront.app.dto.VtVoteRequestRepresentationModel;
import apicross.demo.common.utils.ValidationStages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Service
@Validated({ValidationStages.class})
@Slf4j
public class VotesService {
    public void voteForWork(String workId, @Valid VtVoteRequestRepresentationModel request) {
        log.info("Vote for work id = {}, request type = {}", workId, request.getClass());
    }
}
