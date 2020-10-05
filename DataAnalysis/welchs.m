function [] = welchs(table1, table2, vals1, vals2, alpha)
    % Awaiting time for a call
    diffSE = sqrt(vals1(1,3)^2/length(table1) + ...
    vals2(1,3)^2/length(table2));
    df = (vals1(1,2)/length(table1) + vals2(1,2)/length(table2))^2 / ...
    ((vals1(1,2)/ length(table1))^2/(length(table1)-1)  + ...
    (vals2(1,2)/length(table2))^2 /(length(table2)-1));
    tValue = tinv(alpha/2,df);
    t = (vals1(1,1) - vals2(1,1))/diffSE;
    if (t > -abs(tValue) && t < abs(tValue))
        disp('H0 NOT rejected, i.e. no significant difference between samples means (awaiting time)') 
    else
        disp ('H0 REJECTED, i.e. samples means are significantly different (awaiting time)')
    end
    
    
    % Call duration
    diffSE = sqrt(vals1(2,3)^2/length(table1) + ...
    vals2(2,3)^2/length(table2));
    df = (vals1(2,2)/length(table1) + vals2(2,2)/length(table2))^2 / ...
    ((vals1(2,2)/ length(table1))^2/(length(table1)-1)  + ...
    (vals2(2,2)/length(table2))^2 /(length(table2)-1));
    tValue = tinv(alpha/2,df);
    t = (vals1(2,1) - vals2(2,1))/diffSE;
    if (t > -abs(tValue) && t < abs(tValue))
        disp('H0 NOT rejected, i.e. no significant difference between samples means (call duration)') 
    else
        disp ('H0 REJECTED, i.e. samples means are significantly different (call duration)')
    end
end