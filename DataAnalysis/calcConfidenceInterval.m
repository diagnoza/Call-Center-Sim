function [confidenceInterval] = calcConfidenceInterval(table, vals, alpha)
    pLo = alpha/2;
    pUp = 1 - alpha/2;
    crit = tinv([pLo pUp], length(table) - 1);
    awaitingsConfidenceInterval = vals(1,1) + crit * vals(1,4);
    durationsConfidenceInterval = vals(2,1) + crit * vals(2,4);
    confidenceInterval = [awaitingsConfidenceInterval ; durationsConfidenceInterval];
end