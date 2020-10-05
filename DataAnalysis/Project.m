%% Load data
data_corporate_1 = readmatrix('corporate_1.csv');
data_corporate_2 = readmatrix('corporate_2.csv');
data_consumer_1 = readmatrix('consumers_1.csv');
data_consumer_2 = readmatrix('consumers_2.csv');

%% Calculate relevant values
% INPUT:
% call awaitings column: (:,2) - (:,1)
% call durations column: (:,3) - (:,2)
% OUTPUT:
% awaitings values: (mean,variance,sd,se)
% durations values: (mean,variance,sd,se)
vals_corporate_1 = calcAll(...
    data_corporate_1(:,2) - data_corporate_1(:,1), ...
    data_corporate_1(:,3) - data_corporate_1(:,2));
vals_corporate_2 = calcAll(...
    data_corporate_2(:,2) - data_corporate_2(:,1), ...
    data_corporate_2(:,3) - data_corporate_2(:,2));
vals_consumer_1 = calcAll(...
    data_consumer_1(:,2) - data_consumer_1(:,1), ...
    data_consumer_1(:,3) - data_consumer_1(:,2));
vals_consumer_2 = calcAll(...
    data_consumer_2(:,2) - data_consumer_2(:,1), ...
    data_consumer_2(:,3) - data_consumer_2(:,2));

%% Calculate confidence interval
% OUTPUT:
% awaitings ci
% durations ci
ci_corporate_1 = calcConfidenceInterval(data_corporate_1, vals_corporate_1, 0.01); % alpha = 0.05
ci_corporate_2 = calcConfidenceInterval(data_corporate_2, vals_corporate_2, 0.01);
ci_consumers_1 = calcConfidenceInterval(data_consumer_1, vals_consumer_1, 0.05);
ci_consumers_2 = calcConfidenceInterval(data_consumer_2, vals_consumer_2, 0.05);

%% Welch's t-test for two configurations
welchs(data_corporate_1, data_corporate_2, vals_corporate_1, vals_corporate_2, 0.05) % alpha = 0.05
welchs(data_consumer_1, data_consumer_1, vals_consumer_1, vals_consumer_2, 0.05)

%% Visualisation (waiting time)
% ALL VISUALIZED DATA HAS REMOVED WAITING TIMES = 0 AND OUTLIERS
% (otherwise the graphs wouldn't be readable - the vast majority of 
% calls is being picked up instantly)

figure('Name','Configuration #1');
subplot(2,1,1)
wTime = data_corporate_1(:,2) - data_corporate_1(:,1);
wTime = wTime(wTime > 0); % exclude calls that are picked up immediately
wTime = rmoutliers(wTime);
histogram(wTime)
xlabel('Waiting time')
ylabel('Number of calls')
title('Waiting time for a corporate call to be picked up')

subplot(2,1,2)
wTime = data_consumer_1(:,2) - data_consumer_1(:,1);
wTime = wTime(wTime > 0); % exclude calls that are picked up immediately
wTime = rmoutliers(wTime);
histogram(wTime)
xlabel('Waiting time')
ylabel('Number of calls')
title('Waiting time for a consumer call to be picked up')

figure('Name','Configuration #2');
subplot(2,1,1)
wTime = data_corporate_2(:,2) - data_corporate_2(:,1);
wTime = wTime(wTime > 0); % exclude calls that are picked up immediately
wTime = rmoutliers(wTime);
histogram(wTime)
xlabel('Waiting time')
ylabel('Number of calls')
title('Waiting time for a corporate call to be picked up')

subplot(2,1,2)
wTime = data_consumer_2(:,2) - data_consumer_2(:,1);
wTime = wTime(wTime > 0); % exclude calls that are picked up immediately
wTime = rmoutliers(wTime);
histogram(wTime)
xlabel('Waiting time')
ylabel('Number of calls')
title('Waiting time for a consumer call to be picked up')


%% 
figure('Name','Comparison of consumers waiting times between configurations');
wTime = data_consumer_1(:,2) - data_consumer_1(:,1);
wTime = wTime(wTime > 0); % exclude calls that are picked up immediately
wTime = sort(rmoutliers(wTime));
plot(sort(wTime))
hold on
wTime = data_consumer_2(:,2) - data_consumer_2(:,1);
wTime = wTime(wTime > 0); % exclude calls that are picked up immediately
wTime = sort(rmoutliers(wTime));
plot(sort(wTime))
title('Comparison of consumers waiting times between configurations')
xlabel('Consumer call')
ylabel('Waiting time')
legend('Configuration #1','Configuration #2')
%%
figure('Name','Comparison of corporate waiting times between configurations');
wTime = data_corporate_1(:,2) - data_corporate_1(:,1);
wTime = wTime(wTime > 0); % exclude calls that are picked up immediately
wTime = sort(rmoutliers(wTime));
plot(wTime)
hold on
wTime = data_corporate_2(:,2) - data_corporate_2(:,1);
wTime = wTime(wTime > 0); % exclude calls that are picked up immediately
wTime = sort(rmoutliers(wTime));
plot(wTime)
title('Comparison of corporate waiting times between configurations')
xlabel('Corporate call')
ylabel('Waiting time')
legend('Configuration #1','Configuration #2')