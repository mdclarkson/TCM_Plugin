package net.thecodemaster.sap.verifiers;

import java.util.List;

import net.thecodemaster.sap.constants.Constants;
import net.thecodemaster.sap.points.EntryPoint;
import net.thecodemaster.sap.points.ExitPoint;
import net.thecodemaster.sap.ui.l10n.Messages;

import org.eclipse.jdt.core.dom.Expression;

/**
 * @author Luciano Sampaio
 */
public class SQLInjectionVerifier extends Verifier {

  public SQLInjectionVerifier(List<EntryPoint> entryPoints) {
    super(Messages.Plugin.SQL_INJECTION_VERIFIER_NAME, Constants.Plugin.SQL_INJECTION_VERIFIER_ID, entryPoints);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void run(Expression method, ExitPoint exitPoint) {
  }

}
