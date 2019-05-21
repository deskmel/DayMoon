-- phpMyAdmin SQL Dump
-- version 4.7.7
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: 2019-05-21 05:31:36
-- 服务器版本： 10.1.30-MariaDB
-- PHP Version: 7.2.2

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `daymoon`
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

--
-- 转存表中的数据 `events`
--

INSERT INTO `events` (`eventID`, `userID`, `eventName`, `description`, `beginTime`, `endTime`, `whetherProcess`, `remind`) VALUES
(5, 1, 'Happy things', '见不到', '2019-10-13 10:00:00', '2019-10-13 11:00:00', 1, '{\"foo\": \"bar\", \"bar\": \"foo\"}'),
(52, 1, '几级', '', '2019-05-08 10:00:00', '2019-05-08 11:00:00', 1, '{\"foo\": \"bar\", \"bar\": \"foo\"}'),
(83, 1, '狗', '', '2019-05-14 10:00:00', '2019-05-14 11:00:00', 1, '{\"foo\": \"bar\", \"bar\": \"foo\"}'),
(97, 1, '日', '', '2019-04-15 10:00:00', '2019-04-15 11:00:00', 1, '{\"foo\": \"bar\", \"bar\": \"foo\"}'),
(99, 1, '西瓜皮', '西瓜霜', '2019-06-14 21:00:00', '2019-06-14 21:59:00', 1, '{\"foo\": \"bar\", \"bar\": \"foo\"}'),
(101, 1, '几把', '', '2019-03-16 10:00:00', '2019-03-16 11:00:00', 1, '{\"foo\": \"bar\", \"bar\": \"foo\"}'),
(102, 1, '上海市', '', '2018-08-17 10:00:00', '2018-08-17 11:00:00', 1, '{\"foo\": \"bar\", \"bar\": \"foo\"}'),
(103, 1, '鸡把', '', '2019-09-16 10:00:00', '2019-09-16 11:00:00', 1, '{\"foo\": \"bar\", \"bar\": \"foo\"}'),
(104, 1, '猴', '', '2019-04-16 10:00:00', '2019-04-16 11:00:00', 1, '{\"foo\": \"bar\", \"bar\": \"foo\"}'),
(105, 1, '弟弟', '', '2019-04-16 10:00:00', '2019-04-16 11:00:00', 1, '{\"foo\": \"bar\", \"bar\": \"foo\"}'),
(106, 1, '哥哥', '', '2019-04-30 10:00:00', '2019-04-30 11:00:00', 1, '{\"foo\": \"bar\", \"bar\": \"foo\"}'),
(107, 1, '狗卵', '', '2019-05-18 10:00:00', '2019-05-18 11:00:00', 1, '{\"foo\": \"bar\", \"bar\": \"foo\"}'),
(108, 1, '', '', '2019-05-19 10:00:00', '2019-05-19 11:00:00', 1, '{\"foo\": \"bar\", \"bar\": \"foo\"}');

-- --------------------------------------------------------

--
-- 表的结构 `groupevents`
--

CREATE TABLE `groupevents` (
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
-- 转存表中的数据 `groupevents`
--

INSERT INTO `groupevents` (`eventID`, `groupID`, `dutyUserIDs`, `eventName`, `description`, `beginTime`, `endTime`, `whetherProcess`, `remind`) VALUES
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
  `leaderID` int(11) NOT NULL,
  `imgName` varchar(40) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- 转存表中的数据 `groups`
--

INSERT INTO `groups` (`groupID`, `groupName`, `description`, `memberIDs`, `eventIDs`, `leaderID`, `imgName`) VALUES
(1, 'hhh', '', '[]', '[]', 0, '1722a503-9e6d-4735-b52d-7f41aca85783'),
(2, 'sb', '', '[104, 1]', '[]', 0, '046033c8-81b0-4a39-b37a-b5a1bd2064ef'),
(3, '弟弟组', '', '[]', '[]', 1, '1e10d041-7d68-4835-8fc4-5f25df8674fe'),
(4, '龟孙', '', '[1]', '[]', 1, 'a14c715a-5714-494f-a7d8-4768d1ccef90'),
(5, '狗屎', '', '[1]', '[]', 1, '043cceed-5b7c-49d2-87c8-1f034ab4120c'),
(6, '鸡巴大hh', '', '[1]', '[]', 1, 'f2973692-73e1-44fc-bc7c-9a4e709c1b9d'),
(7, 'hh', '', '[1]', '[]', 1, 'b8c01983-2dac-4924-b62b-e0ca5c517615'),
(8, 'didi', '', '[]', '[]', 1, '34ea8738-39d7-42a2-a439-8075cd0fec83'),
(9, 'didi', '', '[1]', '[]', 1, 'af009e57-ea6f-4689-bb0c-f11c9bdae41e'),
(10, 'uu', '', '[1]', '[]', 1, '3a1c4583-7d91-45b3-9aa0-0cd4a9afc681'),
(11, '空间里', '', '[1]', '[]', 1, 'f4256dc9-cbe4-4b25-975f-9fefd7b98287'),
(12, 'bbb', '', '[1]', '[]', 1, 'e98d9420-f738-4572-a250-e1304a8eedf1'),
(13, 'cnm', '', '[104]', '[]', 104, '35293d30-e206-43b1-9407-d5be4fc72812'),
(14, 'deskmel', '', '[1]', '[]', 1, '35f02948-f660-435c-a880-e727abf50eed'),
(15, 'sg', '', '[1]', '[]', 1, 'eea97383-b897-454a-9851-d8273f4d80d5');

-- --------------------------------------------------------

--
-- 表的结构 `qrcode`
--

CREATE TABLE `qrcode` (
  `qrCodeKey` varchar(40) NOT NULL,
  `groupID` int(11) NOT NULL,
  `startTime` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- 转存表中的数据 `qrcode`
--

INSERT INTO `qrcode` (`qrCodeKey`, `groupID`, `startTime`) VALUES
('12644a05-9196-4642-8234-39ea360a1a88', 2, '2019-05-19 20:03:20'),
('2f1867f9-78eb-4fd7-927a-2193bbfb0d07', 2, '2019-05-19 20:29:20'),
('32c5bce9-1211-4e46-80a1-9cc23de4101e', 2, '2019-05-19 21:43:13'),
('90414319-a113-459f-bbe0-ef602acc4dc7', 2, '2019-05-21 10:32:41'),
('da74df6f-f07d-4ed2-bb26-a86264f8df98', 2, '2019-05-20 21:44:03'),
('e6762948-39cb-4f9f-b151-80c4f62860eb', 2, '2019-05-20 13:06:23');

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
-- 转存表中的数据 `users`
--

INSERT INTO `users` (`userID`, `userName`, `userPassword`, `mailAddress`, `phoneNumber`, `groupIDs`, `eventIDs`) VALUES
(1, 'shen', 'sji', 'jijiba', '2222', '[2, 4, 5, 6, 7, 9, 10, 11, 12, 14, 15]', '[97, 99, 5, 101, 102, 103, 104, 105, 106, 107, 108]'),
(104, 'sss', 'sb', '1', '2', '[2, 13]', '[]'),
(105, 'None', 'None', 'None', 'None', '[]', '[]');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `events`
--
ALTER TABLE `events`
  ADD PRIMARY KEY (`eventID`),
  ADD KEY `eventID` (`eventID`,`eventName`,`description`(255),`beginTime`,`endTime`,`whetherProcess`,`remind`(255)),
  ADD KEY `userID` (`userID`);

--
-- Indexes for table `groupevents`
--
ALTER TABLE `groupevents`
  ADD PRIMARY KEY (`eventID`),
  ADD KEY `eventID` (`eventID`,`eventName`,`description`(255),`beginTime`,`endTime`,`whetherProcess`,`remind`(255)),
  ADD KEY `groupID` (`groupID`,`dutyUserIDs`(255));

--
-- Indexes for table `groups`
--
ALTER TABLE `groups`
  ADD PRIMARY KEY (`groupID`),
  ADD KEY `groupID` (`groupID`,`groupName`,`description`(255),`memberIDs`(255),`eventIDs`(255),`leaderID`);

--
-- Indexes for table `qrcode`
--
ALTER TABLE `qrcode`
  ADD PRIMARY KEY (`qrCodeKey`);

--
-- Indexes for table `users`
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
  MODIFY `eventID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=109;

--
-- 使用表AUTO_INCREMENT `groupevents`
--
ALTER TABLE `groupevents`
  MODIFY `eventID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=34;

--
-- 使用表AUTO_INCREMENT `groups`
--
ALTER TABLE `groups`
  MODIFY `groupID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- 使用表AUTO_INCREMENT `users`
--
ALTER TABLE `users`
  MODIFY `userID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=106;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
