package statistics;

public class StatisticsInvoker {
    private StatisticsCommand command;

    public void setCommand(StatisticsCommand command) {
        this.command = command;
    }

    public String generateReport() {
        if (command == null) return "No report command set.";
        return command.execute();
    }
}
