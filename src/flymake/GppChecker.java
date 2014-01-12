package flymake;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.codan.core.cxx.externaltool.AbstractExternalToolBasedChecker;
import org.eclipse.cdt.codan.core.cxx.externaltool.ConfigurationSettings;
import org.eclipse.cdt.core.IMarkerGenerator;
import org.eclipse.cdt.core.ProblemMarkerInfo;


public class GppChecker extends AbstractExternalToolBasedChecker {
	private static final String ERROR_PROBLEM_ID = "mikami.github.cdt-flymake.error";
	private static final Map<Integer, String> PROBLEM_IDS = new HashMap<Integer, String>();
	static {
	    PROBLEM_IDS.put(IMarkerGenerator.SEVERITY_ERROR_RESOURCE, "mikami.github.cdt-flymake.error"  );
	    PROBLEM_IDS.put(IMarkerGenerator.SEVERITY_WARNING,        "mikami.github.cdt-flymake.warning");
	}

	@Override
	public void addMarker(ProblemMarkerInfo info) {
	    String problemId = PROBLEM_IDS.get(info.severity);
	    String description = String.format("[flymake] %s", info.description);
	    reportProblem(problemId, createProblemLocation(info), description);
	}
    public GppChecker() {
	    super(new ConfigurationSettings("flymake", new File("g++"), "-fsyntax-only -Wall -std=c++0x"));
	}
	@Override
	protected String[] getParserIDs() {
	    return new String[] { "mikami.github.cdt-flymake.GppFlymakeErrorParser" };
	}
	@Override
	protected String getReferenceProblemId() {
	    return ERROR_PROBLEM_ID;
	}

}
