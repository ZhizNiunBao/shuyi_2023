package cn.bywin.business.util;

import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;
import oshi.hardware.Baseboard;
import oshi.hardware.CentralProcessor;
import oshi.hardware.CentralProcessor.TickType;
import oshi.hardware.ComputerSystem;
import oshi.hardware.Display;
import oshi.hardware.Firmware;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HWPartition;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.hardware.PowerSource;
import oshi.hardware.Sensors;
import oshi.hardware.UsbDevice;
import oshi.software.os.FileSystem;
import oshi.software.os.NetworkParams;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;
import oshi.util.Util;

public class SystemInfoTest {
    static Logger LOG = LoggerFactory.getLogger(SystemInfoTest.class);
    public static void main1(String[] args) {
        try {
             LOG.info("java版本号：" + System.getProperty("java.version"));
             LOG.info("Java提供商名称：" + System.getProperty("java.vendor"));
             LOG.info("Java提供商主页：" + System.getProperty("java.vendor.url"));
             LOG.info("jre目录：" + System.getProperty("java.home"));
             LOG.info("Java虚拟机规范版本号：" + System.getProperty("java.vm.specification.version"));
             LOG.info("Java虚拟机规范提供商：" + System.getProperty("java.vm.specification.vendor"));
             LOG.info("Java虚拟机规范名称：" + System.getProperty("java.vm.specification.name"));
             LOG.info("Java虚拟机版本号：" + System.getProperty("java.vm.version"));
             LOG.info("Java虚拟机提供商：" + System.getProperty("java.vm.vendor"));
             LOG.info("Java虚拟机名称：" + System.getProperty("java.vm.name"));
             LOG.info("Java规范版本号：" + System.getProperty("java.specification.version"));
             LOG.info("Java规范提供商：" + System.getProperty("java.specification.vendor"));
             LOG.info("Java规范名称：" + System.getProperty("java.specification.name"));
             LOG.info("Java类版本号：" + System.getProperty("java.class.version"));
             LOG.info("Java类及lib的路径：" + System.getProperty("java.class.path"));
             LOG.info("系统的classpath：" + System.getProperty("java.library.path"));
             LOG.info("Java输入输出临时路径：" + System.getProperty("java.io.tmpdir"));
             LOG.info("Java编译器：" + System.getProperty("java.compiler"));
             LOG.info("Java执行路径：" + System.getProperty("java.ext.dirs"));
             LOG.info("操作系统名称：" + System.getProperty("os.name"));
             LOG.info("操作系统的架构：" + System.getProperty("os.arch"));
             LOG.info("操作系统版本号：" + System.getProperty("os.version"));
             LOG.info("目录分隔符：" + System.getProperty("file.separator"));
             LOG.info("path分隔符：" + System.getProperty("path.separator"));
             LOG.info("直线分隔符：" + System.getProperty("line.separator"));
             LOG.info("操作系统用户名：" + System.getProperty("user.name"));
             LOG.info("当前用户的主目录：" + System.getProperty("user.home"));
             LOG.info("当前程序所在目录：" + System.getProperty("user.dir"));
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        // Options: ERROR > WARN > INFO > DEBUG > TRACE


        LOG.info("Initializing System...");
        SystemInfo si = new SystemInfo();

        HardwareAbstractionLayer hal = si.getHardware();
        OperatingSystem os = si.getOperatingSystem();

         LOG.info("{}",os);

        LOG.info("Checking computer system...");
        printComputerSystem(hal.getComputerSystem());

        LOG.info("Checking Processor...");
        printProcessor(hal.getProcessor());

        LOG.info("Checking Memory...");
        printMemory(hal.getMemory());

        LOG.info("Checking CPU...");
        printCpu(hal.getProcessor());

        LOG.info("Checking Processes...");
        printProcesses(os, hal.getMemory());

        LOG.info("Checking Sensors...");
        printSensors(hal.getSensors());

        LOG.info("Checking Power sources...");
        printPowerSources(hal.getPowerSources());

        LOG.info("Checking Disks...");
        printDisks(hal.getDiskStores());

        LOG.info("Checking File System...");
        printFileSystem(os.getFileSystem());

        LOG.info("Checking Network interfaces...");
        printNetworkInterfaces(hal.getNetworkIFs());

        LOG.info("Checking Network parameterss...");
        printNetworkParameters(os.getNetworkParams());

        // hardware: displays
        LOG.info("Checking Displays...");
        printDisplays(hal.getDisplays());

        // hardware: USB devices
        LOG.info("Checking USB Devices...");
        printUsbDevices(hal.getUsbDevices(true));
    }

    private static void printComputerSystem(final ComputerSystem computerSystem) {

         LOG.info("manufacturer: " + computerSystem.getManufacturer());
         LOG.info("model: " + computerSystem.getModel());
         LOG.info("serialnumber: " + computerSystem.getSerialNumber());
        final Firmware firmware = computerSystem.getFirmware();
         LOG.info("firmware:");
         LOG.info("  manufacturer: " + firmware.getManufacturer());
         LOG.info("  name: " + firmware.getName());
         LOG.info("  description: " + firmware.getDescription());
         LOG.info("  version: " + firmware.getVersion());
         LOG.info("  release date: " + (firmware.getReleaseDate() == null ? "unknown"
                : firmware.getReleaseDate() == null ? "unknown" : firmware.getReleaseDate()));
        final Baseboard baseboard = computerSystem.getBaseboard();
         LOG.info("baseboard:");
         LOG.info("  manufacturer: " + baseboard.getManufacturer());
         LOG.info("  model: " + baseboard.getModel());
         LOG.info("  version: " + baseboard.getVersion());
         LOG.info("  serialnumber: " + baseboard.getSerialNumber());
    }

    private static void printProcessor(CentralProcessor processor) {
         LOG.info("{}",processor);
         LOG.info(" " + processor.getPhysicalPackageCount() + " physical CPU package(s)");
         LOG.info(" " + processor.getPhysicalProcessorCount() + " physical CPU core(s)");
         LOG.info(" " + processor.getLogicalProcessorCount() + " logical CPU(s)");

         LOG.info("Identifier: " + processor.getProcessorIdentifier());
        // LOG.info("ProcessorID: " + processor.getProcessorID());
    }

    private static void printMemory(GlobalMemory memory) {
         LOG.info("Memory: " + FormatUtil.formatBytes(memory.getAvailable()) + "/"
                + FormatUtil.formatBytes(memory.getTotal()));
//         LOG.info("Swap used: " + FormatUtil.formatBytes(memory.getSwapUsed()) + "/"
//                + FormatUtil.formatBytes(memory.getSwapTotal()));
    }

    private static void printCpu(CentralProcessor processor) {
        // LOG.info("Uptime: " + FormatUtil.formatElapsedSecs(processor.getSystemUptime()));
         LOG.info(
                "Context Switches/Interrupts: " + processor.getContextSwitches() + " / " + processor.getInterrupts());

        long[] prevTicks = processor.getSystemCpuLoadTicks();
         LOG.info("CPU, IOWait, and IRQ ticks @ 0 sec:" + Arrays.toString(prevTicks));
        // Wait a second...
        Util.sleep(1000);
        long[] ticks = processor.getSystemCpuLoadTicks();
         LOG.info("CPU, IOWait, and IRQ ticks @ 1 sec:" + Arrays.toString(ticks));
        long user = ticks[TickType.USER.getIndex()] - prevTicks[TickType.USER.getIndex()];
        long nice = ticks[TickType.NICE.getIndex()] - prevTicks[TickType.NICE.getIndex()];
        long sys = ticks[TickType.SYSTEM.getIndex()] - prevTicks[TickType.SYSTEM.getIndex()];
        long idle = ticks[TickType.IDLE.getIndex()] - prevTicks[TickType.IDLE.getIndex()];
        long iowait = ticks[TickType.IOWAIT.getIndex()] - prevTicks[TickType.IOWAIT.getIndex()];
        long irq = ticks[TickType.IRQ.getIndex()] - prevTicks[TickType.IRQ.getIndex()];
        long softirq = ticks[TickType.SOFTIRQ.getIndex()] - prevTicks[TickType.SOFTIRQ.getIndex()];
        long steal = ticks[TickType.STEAL.getIndex()] - prevTicks[TickType.STEAL.getIndex()];
        long totalCpu = user + nice + sys + idle + iowait + irq + softirq + steal;

        System.out.format(
                "User: %.1f%% Nice: %.1f%% System: %.1f%% Idle: %.1f%% IOwait: %.1f%% IRQ: %.1f%% SoftIRQ: %.1f%% Steal: %.1f%%%n",
                100d * user / totalCpu, 100d * nice / totalCpu, 100d * sys / totalCpu, 100d * idle / totalCpu,
                100d * iowait / totalCpu, 100d * irq / totalCpu, 100d * softirq / totalCpu, 100d * steal / totalCpu);
        //System.out.format("CPU load: %.1f%% (counting ticks)%n", processor.getSystemCpuLoadBetweenTicks() * 100);
        //System.out.format("CPU load: %.1f%% (OS MXBean)%n", processor.getSystemCpuLoad() * 100);
        double[] loadAverage = processor.getSystemLoadAverage(3);
         LOG.info("CPU load averages:" + (loadAverage[0] < 0 ? " N/A" : String.format(" %.2f", loadAverage[0]))
                + (loadAverage[1] < 0 ? " N/A" : String.format(" %.2f", loadAverage[1]))
                + (loadAverage[2] < 0 ? " N/A" : String.format(" %.2f", loadAverage[2])));
        // per core CPU
        StringBuilder procCpu = new StringBuilder("CPU load per processor:");
//        double[] load = processor.getProcessorCpuLoadBetweenTicks();
//        for (double avg : load) {
//            procCpu.append(String.format(" %.1f%%", avg * 100));
//        }
//         LOG.info(procCpu.toString());
    }

    private static void printProcesses(OperatingSystem os, GlobalMemory memory) {
         LOG.info("Processes: " + os.getProcessCount() + ", Threads: " + os.getThreadCount());
        // Sort by highest CPU
       // List<OSProcess> procs = Arrays.asList(os.getProcesses(5, ProcessSort.CPU));

         LOG.info("   PID  %CPU %MEM       VSZ       RSS Name");
//        for (int i = 0; i < procs.size() && i < 5; i++) {
//            OSProcess p = procs.get(i);
//            System.out.format(" %5d %5.1f %4.1f %9s %9s %s%n", p.getProcessID(),
//                    100d * (p.getKernelTime() + p.getUserTime()) / p.getUpTime(),
//                    100d * p.getResidentSetSize() / memory.getTotal(), FormatUtil.formatBytes(p.getVirtualSize()),
//                    FormatUtil.formatBytes(p.getResidentSetSize()), p.getName());
//        }
    }

    private static void printSensors(Sensors sensors) {
         LOG.info("Sensors:");
        System.out.format(" CPU Temperature: %.1f°C%n", sensors.getCpuTemperature());
         LOG.info(" Fan Speeds: " + Arrays.toString(sensors.getFanSpeeds()));
        System.out.format(" CPU Voltage: %.1fV%n", sensors.getCpuVoltage());
    }

    private static void printPowerSources(List<PowerSource> powerSources) {
//        StringBuilder sb = new StringBuilder("Power: ");
//        if (powerSources.length == 0) {
//            sb.append("Unknown");
//        } else {
//            double timeRemaining = powerSources[0].getTimeRemaining();
//            if (timeRemaining < -1d) {
//                sb.append("Charging");
//            } else if (timeRemaining < 0d) {
//                sb.append("Calculating time remaining");
//            } else {
//                sb.append(String.format("%d:%02d remaining", (int) (timeRemaining / 3600),
//                        (int) (timeRemaining / 60) % 60));
//            }
//        }
//        for (PowerSource pSource : powerSources) {
//            sb.append(String.format("%n %s @ %.1f%%", pSource.getName(), pSource.getRemainingCapacity() * 100d));
//        }
//         LOG.info(sb.toString());
    }

    private static void printDisks(List<HWDiskStore> diskStores) {
         LOG.info("Disks:");
        for (HWDiskStore disk : diskStores) {
            boolean readwrite = disk.getReads() > 0 || disk.getWrites() > 0;
            System.out.format(" %s: (model: %s - S/N: %s) size: %s, reads: %s (%s), writes: %s (%s), xfer: %s ms%n",
                    disk.getName(), disk.getModel(), disk.getSerial(),
                    disk.getSize() > 0 ? FormatUtil.formatBytesDecimal(disk.getSize()) : "?",
                    readwrite ? disk.getReads() : "?", readwrite ? FormatUtil.formatBytes(disk.getReadBytes()) : "?",
                    readwrite ? disk.getWrites() : "?", readwrite ? FormatUtil.formatBytes(disk.getWriteBytes()) : "?",
                    readwrite ? disk.getTransferTime() : "?");
            List<HWPartition> partitions = disk.getPartitions();
            if (partitions == null) {
                // TODO Remove when all OS's implemented
                continue;
            }
            for (HWPartition part : partitions) {
                System.out.format(" |-- %s: %s (%s) Maj:Min=%d:%d, size: %s%s%n", part.getIdentification(),
                        part.getName(), part.getType(), part.getMajor(), part.getMinor(),
                        FormatUtil.formatBytesDecimal(part.getSize()),
                        part.getMountPoint().isEmpty() ? "" : " @ " + part.getMountPoint());
            }
        }
    }

    private static void printFileSystem(FileSystem fileSystem) {
         LOG.info("File System:");

        System.out.format(" File Descriptors: %d/%d%n", fileSystem.getOpenFileDescriptors(),
                fileSystem.getMaxFileDescriptors());

        List<OSFileStore> fsArray = fileSystem.getFileStores();
        for (OSFileStore fs : fsArray) {
            long usable = fs.getUsableSpace();
            long total = fs.getTotalSpace();
            System.out.format(
                    " %s (%s) [%s] %s of %s free (%.1f%%) is %s "
                            + (fs.getLogicalVolume() != null && fs.getLogicalVolume().length() > 0 ? "[%s]" : "%s")
                            + " and is mounted at %s%n",
                    fs.getName(), fs.getDescription().isEmpty() ? "file system" : fs.getDescription(), fs.getType(),
                    FormatUtil.formatBytes(usable), FormatUtil.formatBytes(fs.getTotalSpace()), 100d * usable / total,
                    fs.getVolume(), fs.getLogicalVolume(), fs.getMount());
        }
    }

    private static void printNetworkInterfaces(List<NetworkIF> networkIFs) {
         LOG.info("Network interfaces:");
        for (NetworkIF net : networkIFs) {
            System.out.format(" Name: %s (%s)%n", net.getName(), net.getDisplayName());
            System.out.format("   MAC Address: %s %n", net.getMacaddr());
            System.out.format("   MTU: %s, Speed: %s %n", net.getMTU(), FormatUtil.formatValue(net.getSpeed(), "bps"));
            System.out.format("   IPv4: %s %n", Arrays.toString(net.getIPv4addr()));
            System.out.format("   IPv6: %s %n", Arrays.toString(net.getIPv6addr()));
            boolean hasData = net.getBytesRecv() > 0 || net.getBytesSent() > 0 || net.getPacketsRecv() > 0
                    || net.getPacketsSent() > 0;
            System.out.format("   Traffic: received %s/%s%s; transmitted %s/%s%s %n",
                    hasData ? net.getPacketsRecv() + " packets" : "?",
                    hasData ? FormatUtil.formatBytes(net.getBytesRecv()) : "?",
                    hasData ? " (" + net.getInErrors() + " err)" : "",
                    hasData ? net.getPacketsSent() + " packets" : "?",
                    hasData ? FormatUtil.formatBytes(net.getBytesSent()) : "?",
                    hasData ? " (" + net.getOutErrors() + " err)" : "");
        }
    }

    private static void printNetworkParameters(NetworkParams networkParams) {
         LOG.info("Network parameters:");
        System.out.format(" Host name: %s%n", networkParams.getHostName());
        System.out.format(" Domain name: %s%n", networkParams.getDomainName());
        System.out.format(" DNS servers: %s%n", Arrays.toString(networkParams.getDnsServers()));
        System.out.format(" IPv4 Gateway: %s%n", networkParams.getIpv4DefaultGateway());
        System.out.format(" IPv6 Gateway: %s%n", networkParams.getIpv6DefaultGateway());
    }

    private static void printDisplays(List<Display> displays) {
         LOG.info("Displays:");
        int i = 0;
        for (Display display : displays) {
             LOG.info(" Display " + i + ":");
             LOG.info(display.toString());
            i++;
        }
    }

    private static void printUsbDevices(List<UsbDevice> usbDevices) {
         LOG.info("USB Devices:");
        for (UsbDevice usbDevice : usbDevices) {
             LOG.info(usbDevice.toString());
        }
    }


}
