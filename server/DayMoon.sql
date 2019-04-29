-- phpMyAdmin SQL Dump
-- version 4.8.5
-- https://www.phpmyadmin.net/
--
-- 主机： localhost
-- 生成日期： 2019-04-25 13:36:34
-- 服务器版本： 10.1.38-MariaDB
-- PHP 版本： 7.1.28

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- 数据库： `DayMoon`
--

-- --------------------------------------------------------

--
-- 表的结构 `events`
--

CREATE TABLE `events` (
  `eventID` int(11) NOT NULL,
  `userID` int(11) NOT NULL,
  `eventName` varchar(50) NOT NULL,
  `description` varchar(1000) NOT NULL,
  `beginTime` datetime NOT NULL,
  `endTime` datetime NOT NULL,
  `whetherProcess` tinyint(1) NOT NULL,
  `remind` mediumtext NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `groupEvents`
--

CREATE TABLE `groupEvents` (
  `eventID` int(11) NOT NULL,
  `groupID` int(11) NOT NULL,
  `dutyUserIDs` varchar(10000) NOT NULL,
  `eventName` varchar(50) NOT NULL,
  `description` varchar(1000) NOT NULL,
  `beginTime` datetime NOT NULL,
  `endTime` datetime NOT NULL,
  `whetherProcess` tinyint(1) NOT NULL,
  `remind` mediumtext NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- 转存表中的数据 `groupEvents`
--

INSERT INTO `groupEvents` (`eventID`, `groupID`, `dutyUserIDs`, `eventName`, `description`, `beginTime`, `endTime`, `whetherProcess`, `remind`) VALUES
(33, 60, '[82, 83, 85]', '一起睡觉', '一起香', '2019-04-25 00:00:00', '2019-04-25 10:00:00', 1, '{}');

-- --------------------------------------------------------

--
-- 表的结构 `groups`
--

CREATE TABLE `groups` (
  `groupID` int(11) NOT NULL,
  `groupName` varchar(50) NOT NULL,
  `description` varchar(1000) NOT NULL,
  `memberIDs` varchar(10000) NOT NULL,
  `eventIDs` varchar(10000) NOT NULL,
  `leaderID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `users`
--

CREATE TABLE `users` (
  `userID` int(11) NOT NULL,
  `userName` varchar(50) NOT NULL,
  `userPassword` char(32) NOT NULL,
  `mailAddress` varchar(50) NOT NULL,
  `phoneNumber` varchar(20) NOT NULL,
  `groupIDs` varchar(10000) NOT NULL,
  `eventIDs` varchar(10000) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- 转储表的索引
--

--
-- 表的索引 `events`
--
ALTER TABLE `events`
  ADD PRIMARY KEY (`eventID`),
  ADD KEY `eventID` (`eventID`,`eventName`,`description`(255),`beginTime`,`endTime`,`whetherProcess`,`remind`(255)),
  ADD KEY `userID` (`userID`);

--
-- 表的索引 `groupEvents`
--
ALTER TABLE `groupEvents`
  ADD PRIMARY KEY (`eventID`),
  ADD KEY `eventID` (`eventID`,`eventName`,`description`(255),`beginTime`,`endTime`,`whetherProcess`,`remind`(255)),
  ADD KEY `groupID` (`groupID`,`dutyUserIDs`(255));

--
-- 表的索引 `groups`
--
ALTER TABLE `groups`
  ADD PRIMARY KEY (`groupID`),
  ADD KEY `groupID` (`groupID`,`groupName`,`description`(255),`memberIDs`(255),`eventIDs`(255),`leaderID`);

--
-- 表的索引 `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`userID`),
  ADD UNIQUE KEY `userName` (`userName`),
  ADD UNIQUE KEY `mailAddress` (`mailAddress`),
  ADD UNIQUE KEY `phoneNumber` (`phoneNumber`),
  ADD KEY `userID` (`userID`,`userName`,`userPassword`,`mailAddress`,`phoneNumber`,`groupIDs`(255)),
  ADD KEY `eventIDs` (`eventIDs`(255));

--
-- 在导出的表使用AUTO_INCREMENT
--

--
-- 使用表AUTO_INCREMENT `events`
--
ALTER TABLE `events`
  MODIFY `eventID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=25;

--
-- 使用表AUTO_INCREMENT `groupEvents`
--
ALTER TABLE `groupEvents`
  MODIFY `eventID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=34;

--
-- 使用表AUTO_INCREMENT `groups`
--
ALTER TABLE `groups`
  MODIFY `groupID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=61;

--
-- 使用表AUTO_INCREMENT `users`
--
ALTER TABLE `users`
  MODIFY `userID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=104;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
