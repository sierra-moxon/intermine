package org.intermine.webservice.server.idresolution;

import java.util.Arrays;
import java.util.Map;

import org.intermine.api.InterMineAPI;
import org.intermine.webservice.server.core.JSONService;
import org.intermine.webservice.server.exceptions.ResourceNotFoundException;
import org.intermine.webservice.server.idresolution.Job.JobStatus;
import org.intermine.webservice.server.output.JSONFormatter;

public class JobStatusService extends JSONService
{

    private final String jobId;

    public JobStatusService(InterMineAPI im, String jobId) {
        super(im);
        this.jobId = jobId;
    }

    @Override
    protected void execute() throws Exception {
        Job job = Job.getJobById(jobId);
        if (job != null) {
            if (job.getStatus() == JobStatus.ERROR) {
                this.addOutputInfo("message", job.getError().getMessage());
            }
            addResultValue(job.getStatus().name(), false);
        } else {
            throw new ResourceNotFoundException("No such job: " + jobId);
        }
    }

    @Override
    protected String getResultsKey() {
        return "status";
    }

}
