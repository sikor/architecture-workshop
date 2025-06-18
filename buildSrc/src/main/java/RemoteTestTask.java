import org.gradle.api.tasks.testing.Test;
import org.gradle.process.ExecOperations;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.File;

public abstract class RemoteTestTask extends Test {

    private final ExecOperations execOps;

    @Inject
    public RemoteTestTask(ExecOperations execOps) {
        super();
        this.execOps = execOps;

        useJUnitPlatform();

        doFirst(task -> {
            String accountCheck = execAndCapture(new String[]{"az", "account", "show"}, new File("."));
            if (accountCheck.contains("Please run 'az login'")) {
                System.out.println("🔐 Logging into Azure...");
                execOps.exec(spec -> spec.commandLine("az", "login"));
            } else {
                System.out.println("✅ Already logged into Azure.");
            }

            System.out.println("🚀 Running terraform init...");
            execOps.exec(spec -> {
                spec.setWorkingDir(getProject().file("../infra"));
                spec.commandLine("terraform", "init", "-input=false");
            });

            System.out.println("📥 Reading terraform outputs...");
            environment("TOKEN_URI", terraformOutput("token_uri"));
            environment("CLIENT_ID", terraformOutput("e2e_client_id"));
            environment("CLIENT_SECRET", terraformOutput("e2e_client_secret"));
            environment("EVENTS_API_BASE_URL", terraformOutput("events_app_url"));
            environment("AGGREGATOR_API_BASE_URL", terraformOutput("aggregator_app_url"));
            environment("EVENTS_APP_SCOPE", terraformOutput("events_app_client_credentials_scope"));
            environment("AGGREGATOR_APP_SCOPE", terraformOutput("aggregator_app_client_credentials_scope"));
        });
    }

    private String execAndCapture(String[] command, File workingDir) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        execOps.exec(spec -> {
            spec.setWorkingDir(workingDir);
            spec.commandLine((Object[]) command);
            spec.setIgnoreExitValue(true);
            spec.setStandardOutput(output);
            spec.setErrorOutput(output);
        });
        return output.toString().trim();
    }

    private String terraformOutput(String name) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        execOps.exec(spec -> {
            spec.setWorkingDir(getProject().file("../infra"));
            spec.commandLine("terraform", "output", "-raw", name);
            spec.setStandardOutput(output);
        });
        return output.toString().trim();
    }
}
