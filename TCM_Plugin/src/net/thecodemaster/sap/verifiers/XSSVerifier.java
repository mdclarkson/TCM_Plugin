package net.thecodemaster.sap.verifiers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.thecodemaster.sap.exitpoints.ExitPoint;
import net.thecodemaster.sap.graph.Parameter;
import net.thecodemaster.sap.ui.l10n.Messages;
import net.thecodemaster.sap.utils.Creator;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;

/**
 * @author Luciano Sampaio
 */
public class XSSVerifier extends Verifier {

  public XSSVerifier() {
    super(Messages.Plugin.XSS_VERIFIER);
  }

  static {
    // 01 - Create each ExitPoint.
    Map<Parameter, List<Integer>> params01 = Creator.newMap();
    // Only sanitized values and STRING_LITERAL are valid.
    params01.put(new Parameter("java.lang.String"), Arrays.asList(ASTNode.STRING_LITERAL));

    ExitPoint ep01 = new ExitPoint("java.io.PrintWriter", "println");
    ep01.setParameters(params01);

    // 01.1 - Add the ExitPoint to the list.
    getListExitPoints().add(ep01);

    // 02 ExitPoint
    // 02.1 - Add the ExitPoint to the list.
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void run(Expression method, ExitPoint exitPoint) {

  }

}
