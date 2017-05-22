<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%
    final String root = request.getContextPath() + "/static/";
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta content="width=device-width,initial-scale=1" name="viewport">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css" integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp" crossorigin="anonymous">
    <link rel="stylesheet" href="<%= root %>site-css.css"/>
    <title>Friend Stalker</title>
</head>
<body>
<div class="container-fluid">
    <header>
        <h1>Friend Stalker</h1>
    </header>
    <form id="login-form" data-bind="slideVisible: !loggedIn()" class="form-inline">
        <div class="form-group">
            <label for="my-username">Username:</label>
            <input id="my-username" required maxlength="255" autofocus
                   data-bind="textInput: username, enable: !loggingIn()" class="form-control"/>
            <input type="submit" value="Log in" class="btn btn-sm btn-primary" data-bind="enable: loginButtonEnabled"/>
        </div>
        <div data-bind="if: autologinSupported">
            <label>
                <input type="checkbox" data-bind="checked: autologin"/>
                <span>Keep me logged in</span>
            </label>
        </div>
        <div data-bind="text: logInMessage, css: logInClass"></div>
    </form>
    <div data-bind="slideVisible: loggedIn" style="display:none">
        <div class="text-success">
            <span>Hi,</span>
            <span data-bind="text: username" class="after-exclaim"></span>
            <button type="button" class="btn btn-xs btn-primary" id="log-out">Log out</button>
        </div>
        <hr/>
        <div id="tabs">
            <ul class="nav nav-tabs">
                <li class="active"><a href="#subscriptions" data-toggle="tab">Subscription Requests</a></li>
                <li><a href="#map" data-toggle="tab">Map</a></li>
            </ul>
            <div class="tab-content">
                <div class="tab-pane fade in active" id="subscriptions">
                    <form id="send-subscription-request" class="form-inline">
                        <div class="form-group">
                            <label for="subscription-request-recipient">Recipient:</label>
                            <input id="subscription-request-recipient" class="form-control" required
                                   data-bind="textInput: subscriptionRequestRecipient, enable: !subscriptionRequestSending()"/>
                            <input type="submit" value="Send subscription request" class="btn btn-sm btn-primary"
                                   data-bind="enable: subscriptionRequestButtonEnabled"/>
                        </div>
                        <div data-bind="text: subscriptionRequestMessage, css: subscriptionRequestClass"></div>
                    </form>
                    <hr/>
                    <div class="col-sm-6">
                        <h3>
                            <span>Incoming requests</span>
                            <button type="button" class="btn btn-xs btn-primary" id="refresh-inc-subscriptions"
                                    data-bind="enable: !incSubscriptionRefreshed()">Refresh
                            </button>
                        </h3>
                        <ul id="inc-subscriptions-list" data-bind="foreach: incSubscriptionRequests" class="list-group">
                            <li class="list-group-item clearfix" data-bind="attr: {'data-id': subscriberId}">
								<span class="pull-left">
									<strong data-bind="text: subscriberId"></strong>
									<time data-bind="longdate: timeStamp" class="after-bracket before-bracket"></time>
								</span>
                                <div class="btn-group btn-group-xs pull-right">
                                    <button type="button" class="btn btn-success" data-act="accept">Accept</button>
                                    <button type="button" class="btn btn-danger" data-act="deny">Deny</button>
                                </div>
                            </li>
                        </ul>
                        <div data-bind="visible: !incSubscriptionRequests().length" class="alert alert-info">
                            You have no incoming subscription requests
                        </div>
                    </div>
                    <div class="col-sm-6">
                        <h3>
                            <span>Outgoing requests</span>
                            <button type="button" class="btn btn-xs btn-primary" id="refresh-out-subscriptions"
                                    data-bind="enable: !outSubscriptionRefreshed()">Refresh
                            </button>
                        </h3>
                        <ul id="out-subscriptions-list" data-bind="foreach: outSubscriptionRequests" class="list-group">
                            <li class="list-group-item clearfix" data-bind="attr: {'data-id': subscribeTo}">
								<span class="pull-left">
									<strong data-bind="text: subscribeTo"></strong>
									<time data-bind="longdate: timeStamp" class="after-bracket before-bracket"></time>
								</span>
                                <div class="btn-group btn-group-xs pull-right">
                                    <button type="button" class="btn btn-danger" data-act="cancel">Cancel</button>
                                </div>
                            </li>
                        </ul>
                        <div data-bind="visible: !outSubscriptionRequests().length" class="alert alert-info">
                            You have no outgoing subscription requests
                        </div>
                    </div>
                </div>
                <div class="tab-pane fade" id="map">
                    <div class="text-info margin-bottom" data-bind="with: lastCheckIn">
                        <div data-bind="if: exists">
                            <span>Your last check-in was at</span>
                            <span class="label label-default">Lat</span>
                            <span data-bind="text: lat"></span>
                            <span class="label label-default">Long</span>
                            <span data-bind="text: long"></span>
                        </div>
                        <div data-bind="if: !exists()" class="text-info">
                            No check-ins to date
                        </div>
                    </div>
                    <div>
                        <form class="form form-inline" id="checkin-form">
                            <div class="form-group">
                                <div class="input-group">
                                    <div class="input-group-addon" data-bind="css: checkInLatClass">Lat</div>
                                    <input class="form-control" required placeholder="Fill me in!" data-bind="textInput: checkInLatitude, css: checkInLatClass"/>
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="input-group">
                                    <div class="input-group-addon" data-bind="css: checkInLongClass">Long</div>
                                    <input class="form-control" required placeholder="Fill me in!" data-bind="textInput: checkInLongitude, css: checkInLongClass"/>
                                </div>
                            </div>
                            <div class="form-group">
                                <input type="submit" class="btn btn-primary" value="Check-in" data-bind="enable: checkInBtnEnabled"/>
                            </div>
                        </form>
                    </div>
                    <hr/>
                    <div class="col-md-3 col-lg-2" id="friend-list-container">
                        <h3>
                            <span>Friends</span>
                            <button class="btn btn-xs btn-primary" type="button" id="refresh-friends" data-bind="disable: friendsBeingRefreshed">Refresh</button>
                        </h3>
                        <div id="friend-list" class="list-group" data-bind="slideVisible: friends().length, foreach: friends">
                            <div class="list-group-item pointer" data-bind="attr: {title: tooltip, 'data-user-id': id}">
                                <strong data-bind="text: id" class="show text-center"></strong>
                            </div>
                        </div>
                        <div class="alert alert-info" data-bind="slideVisible: !friends().length">
                            You have no friends...
                        </div>
                    </div>
                    <div class="col-md-9 col-lg-10 map-container" id="map-container"></div>
                </div>
            </div>
        </div>
    </div>
</div>
<script type="application/javascript" src="https://cdn.jsdelivr.net/bluebird/latest/bluebird.min.js"></script>
<script type="application/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
<script type="application/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/knockout/3.4.2/knockout-min.js"></script>
<script type="application/javascript" src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
<script type="application/javascript" src="<%= root %>out.js" async></script>
</body>
</html>