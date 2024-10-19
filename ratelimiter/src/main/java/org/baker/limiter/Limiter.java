package org.baker.limiter;

import java.util.List;
import org.baker.model.Request;
import org.baker.model.Response;

public interface Limiter {
  Response tryRequest(Request request);
}
