package com.github.vzakharchenko.radius.coa;

import org.tinyradius.dictionary.Dictionary;

public interface IRadiusCoAClient {
    void requestCoA(Dictionary dictionary,
                    ICoaRequestHandler coaRequestHandler);
}
