package flymake;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.codan.core.cxx.externaltool.AbstractExternalToolBasedChecker;
import org.eclipse.cdt.codan.core.cxx.externaltool.ConfigurationSettings;
import org.eclipse.cdt.codan.core.param.MapProblemPreference;
import org.eclipse.cdt.core.IMarkerGenerator;
import org.eclipse.cdt.core.ProblemMarkerInfo;
import org.eclipse.cdt.core.model.IIncludeReference;
import org.eclipse.cdt.internal.core.model.CProject;
import org.eclipse.core.resources.IResource;

/**
 * TODO: リファクタリングしないとしょうもなさすぎ。
 * @author mikami
 *
 */
@SuppressWarnings("restriction")
public class GppChecker extends AbstractExternalToolBasedChecker {
    private static final String ERROR_PROBLEM_ID = "mikami.github.cdt-flymake.error";
	private static final Map<Integer, String> PROBLEM_IDS = new HashMap<Integer, String>();
	static {
	    PROBLEM_IDS.put(IMarkerGenerator.SEVERITY_ERROR_RESOURCE, "mikami.github.cdt-flymake.error"  );
	    PROBLEM_IDS.put(IMarkerGenerator.SEVERITY_WARNING,        "mikami.github.cdt-flymake.warning");
	}
	
	private static final String defaultArgs = "-fsyntax-only -Wall -std=c++0x";
	private static final ConfigurationSettings settings = 
			new ConfigurationSettings("flymake", new File("g++"), defaultArgs);
	
    public GppChecker() {
    	super(settings);
	}

    @Override 
    public boolean processResource(IResource resource) {
    	try{
		    MapProblemPreference preference = (MapProblemPreference) getProblemById(getReferenceProblemId(), resource).getPreference();
		    /**
		     * TODO 変更を一切保存しないことになる。
		     */
    	    String path = defaultArgs;//preference.getChildDescriptor(settings.getArgs().getDescriptor().getKey()).getValue();
			IIncludeReference[] includeLists = new CProject(null, resource.getProject()).getIncludeReferences();
		    for(int i = 0; i < includeLists.length; i++){
			    IIncludeReference test = includeLists[i];
			    if(!path.contains("-I" + test.getPath().toOSString())){
				    path += " -I" + test.getPath().toOSString();
			    }
		    }
		    /*値の保存*/
		    preference.getChildDescriptor(settings.getArgs().getDescriptor().getKey()).setValue(path);
		    
		    return super.processResource(resource);
    	}catch (Exception e){
    		return false;
    	}
    }
    @Override
    public void addMarker(ProblemMarkerInfo info) {
      String problemId = PROBLEM_IDS.get(info.severity);
      String description = String.format("[flymake] %s", info.description);
      reportProblem(problemId, createProblemLocation(info), description);
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
