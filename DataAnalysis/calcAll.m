function [res] = calcAll(call_awaitings, call_durations)
    
    call_awaitings_mean = mean(call_awaitings);
    call_awaitings_variance = var(call_awaitings);
    call_awaitings_sd = sqrt(call_awaitings_variance);
    call_awaitings_se = call_awaitings_sd/sqrt(length(call_awaitings));
    
    call_durations_mean = mean(call_durations);
    call_durations_variance = var(call_durations);
    call_durations_sd = sqrt(call_durations_variance);
    call_durations_se = call_durations_sd/sqrt(length(call_durations));
    
    % Check requirements (it's a double check, since this should already be
    % ensured by the Java code)
    % x = 300 % 300s = 5 minutes
    % C = call_awaitings < x; % amount of customers served under x
    % seconds
    % X = size(call_awaitings(C))/size(call_awaitings) % of customers
    % served under
    
    res = [...
        call_awaitings_mean, call_awaitings_variance, call_awaitings_sd, call_awaitings_se; ...
        call_durations_mean, call_durations_variance, call_durations_sd, call_durations_se...
        ];
     % res = call_awaitings;
end