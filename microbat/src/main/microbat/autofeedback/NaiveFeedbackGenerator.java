package microbat.autofeedback;

import java.util.List;

import org.eclipse.swt.widgets.Display;

import microbat.model.trace.Trace;
import microbat.model.trace.TraceNode;
import microbat.model.value.VarValue;
import microbat.recommendation.ChosenVariableOption;
import microbat.recommendation.UserFeedback;
import microbat.views.DebugFeedbackView;
import microbat.views.MicroBatViews;

public final class NaiveFeedbackGenerator extends FeedbackGenerator {

	/**
	 * Feedback of previous node in trace. Need to be initialize by users.
	 */
	private UserFeedback prevFeedback;
	
	public NaiveFeedbackGenerator(Trace trace, AutoFeedbackMethods method) {
		super(trace, method);
		this.prevFeedback = null;
	}

	/**
	 * Give feedback based on the previous feedback. Can be null.
	 */
	@Override
	public UserFeedback giveFeedback(TraceNode node) {
		
		// If the previous feedback is not available, then get one in the feedback view of microbat
		if (this.prevFeedback == null) {
			this.updateFeedbackFromView();
			this.prevFeedback = this.feedbackFromView;
		}
		
		if (this.prevFeedback.getFeedbackType() == null) {
			System.out.println("Please provide initial feedback before using NAIVE approach.");
			return null;
		}
		
		UserFeedback feedback = new UserFeedback();
		// If the previous feedback is data incorrect, we need to randomly pick one variable for current node.
		if(this.prevFeedback.getFeedbackType() == UserFeedback.WRONG_VARIABLE_VALUE) {
			List<VarValue> readVars = node.getReadVariables();
			readVars = this.removeVarsGenByJava(readVars);	// Remove variables that is generated by java
			if (readVars.isEmpty()) {
				// If the current node do not have any read variable, then change it to control incorrect from now on.
				feedback.setFeedbackType(UserFeedback.WRONG_PATH);
			} else {
				// Select random variable to be wrong
				VarValue wrongVar = this.getRandVar(readVars, true);
				ChosenVariableOption option = new ChosenVariableOption(wrongVar, null);
				feedback.setFeedbackType(UserFeedback.WRONG_VARIABLE_VALUE);
				feedback.setOption(option);
			}
		} else {
			// Just directly copy if the previous feedback is control incorrect.
			feedback.setFeedbackType(UserFeedback.WRONG_PATH);
		}
		this.printFeedbackMessage(node, feedback);
		return feedback;
	}
}
