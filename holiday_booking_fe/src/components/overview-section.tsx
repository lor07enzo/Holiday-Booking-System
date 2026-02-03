import { Bed, Home, TrendingUp, Users } from "lucide-react"
import { Card, CardContent, CardHeader, CardTitle } from "./ui/card"


interface OverviewProps {
    totalHabitations: number
    totalHosts: number
    totalReservations: number
    mediaNumberRooms: number
}

export const OverviewSection = (
    { totalHabitations, totalHosts, totalReservations, mediaNumberRooms: mediaNumberBeds }: OverviewProps) => {

    const stats = [
        {
            title: "Total Habitations",
            value: totalHabitations,
            icon: Home,
            description: "In the system"
        },
        {
            title: "Total Hosts",
            value: totalHosts,
            icon: Users,
            description: "In the system"
        },
        {
            title: "Reservations",
            value: totalReservations,
            icon: TrendingUp,
            description: "Last month"
        },
        {
            title: "Average number of beds",
            value: mediaNumberBeds,
            icon: Bed,
            description: "For habitations"
        },
    ]

    return (
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
            {stats.map((stat) => (
                <Card
                    key={stat.title}
                    className="cursor-pointer hover:border-blue-900 hover:shadow-md"
                >
                    <CardHeader className="flex flex-row items-center justify-between pb-2">
                        <CardTitle className="text-sm font-medium text-muted-foreground">{stat.title}</CardTitle>
                        <stat.icon className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent className="pt-0">
                        <div className="text-2xl font-bold">{stat.value}</div>
                        <p className="text-xs text-muted-foreground">
                            {stat.description}
                        </p>
                    </CardContent>
                </Card>
            ))}
        </div>
    )
}