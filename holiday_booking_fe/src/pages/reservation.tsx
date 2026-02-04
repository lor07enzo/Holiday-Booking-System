import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Calendar } from "@/components/ui/calendar";
import { Field, FieldGroup, FieldLabel } from "@/components/ui/field";
import { Input } from "@/components/ui/input";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { Select, SelectContent, SelectGroup, SelectItem, SelectLabel, SelectTrigger, SelectValue } from "@/components/ui/select";
import { format } from "date-fns";
import { CalendarIcon } from "lucide-react";
import { useState } from "react";
import { toast } from "sonner";
import { date, z } from "zod";
import type { DateRange } from "react-day-picker";
import { useUsers } from "@/context/user-context";

const CreateReservationFormSchema = z.object({
    habitation: z.number(),
    user: z.number(),
    status: z.string().min(1, "").max(5, ""),
    startDate: z.date,
    endDate: z.date
});

export type CreateReservationFormData = z.infer<typeof CreateReservationFormSchema>;

export function Reservation() {
    const [errors, setErrors] = useState<Record<string, string>>({});
    const { users, hosts, habitations } = useUsers();
    const [date, setDate] = useState<DateRange | undefined>(undefined);

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        const data = {}

         const parsed = createHabitationFormSchema.safeParse(data);

        if (!parsed.success) {
            const fieldErrors: Record<string, string> = {};
            parsed.error.issues.forEach(issue => {
                fieldErrors[issue.path[0] as string] = issue.message;
            });
            setErrors(fieldErrors);
            return;
        }

        setErrors({});
    }

    return (
        <div className=" w-full flex flex-col h-screen justify-center items-center">
            <Card className="w-full max-w-4xl">
                <CardHeader>
                    <CardTitle>Create a New Reservation</CardTitle>
                    <CardDescription>
                        Enter your information below to create your reservation.
                    </CardDescription>
                </CardHeader>
                <CardContent>
                    <form onSubmit={handleSubmit}>
                        <FieldGroup >
                            <div className="grid grid-cols-2 gap-4">
                                <Field className="gap-1">
                                    <FieldLabel htmlFor="hostCode">Host Code</FieldLabel>
                                    <Select name="hostCode" value={selectedHostCode} onValueChange={setSelectedHostCode}>
                                        <SelectTrigger className="w-full " aria-invalid={!!errors.hostCode}>
                                            <SelectValue placeholder="Select a Host" />
                                        </SelectTrigger>
                                        <SelectContent>
                                            <SelectGroup>
                                                <SelectLabel>Hosts</SelectLabel>
                                                {hosts.length === 0 ? ( 
                                                    <SelectItem value="null">No hosts Found</SelectItem> 
                                                ) : (
                                                    hosts.map(host => (
                                                        <SelectItem key={host.hostCode} value={String(host.hostCode)}>
                                                            Host - {host.hostCode}
                                                        </SelectItem>
                                                    ))
                                                )}
                                            </SelectGroup>
                                        </SelectContent>
                                    </Select>
                                    {errors.hostCode && <p className="text-red-500 text-sm">{errors.hostCode}</p>}
                                </Field>

                                <Field className="gap-1">
                                    <FieldLabel htmlFor="name">Name</FieldLabel>
                                    <Input id="name" name="name" placeholder="Habitation in Via Roma 56" aria-invalid={!!errors.name} />
                                    {errors.name && <p className="text-red-500 text-sm">{errors.name}</p>}
                                </Field>

                                <Field className="gap-1">
                                    <FieldLabel htmlFor="description">Description</FieldLabel>
                                    <Input 
                                        id="description" 
                                        name="description" 
                                        placeholder="Description of the house" 
                                        aria-invalid={!!errors.description} />
                                    {errors.description && <p className="text-red-500 text-sm">{errors.description}</p>}
                                </Field>

                                <Field className="gap-1">
                                    <FieldLabel htmlFor="street">Street</FieldLabel>
                                    <Input id="street" name="street" placeholder="Via Roma, 56" aria-invalid={!!errors.street} />
                                    {errors.street && <p className="text-red-500 text-sm">{errors.street}</p>}
                                </Field>

                                <Field className="gap-1">
                                    <FieldLabel htmlFor="city">City</FieldLabel>
                                    <Input id="city" name="city" placeholder="Firenze" aria-invalid={!!errors.city} />
                                    {errors.city && <p className="text-red-500 text-sm">{errors.city}</p>}
                                </Field>

                                <Field className="gap-1">
                                    <FieldLabel htmlFor="country">Country</FieldLabel>
                                    <Input id="country" name="country" placeholder="Italy" aria-invalid={!!errors.country} />
                                    {errors.country && <p className="text-red-500 text-sm">{errors.country}</p>}
                                </Field>

                                <Field className="gap-1">
                                    <FieldLabel htmlFor="floor">Number of Floors</FieldLabel>
                                    <Input id="floor" type="number" name="floor" placeholder="ex. 1" aria-invalid={!!errors.floor} />
                                    {errors.floor && <p className="text-red-500 text-sm">{errors.floor}</p>}
                                </Field>

                                <Field className="gap-1">
                                    <FieldLabel htmlFor="rooms">Number of Rooms</FieldLabel>
                                    <Input id="rooms" type="number" name="rooms" placeholder="ex. 2" aria-invalid={!!errors.rooms} />
                                    {errors.rooms && <p className="text-red-500 text-sm">{errors.rooms}</p>}
                                </Field>

                                <Field className="gap-1">
                                    <FieldLabel htmlFor="price">Price for Night</FieldLabel>
                                    <Input id="price" type="number" name="price" placeholder="€50" aria-invalid={!!errors.price} />
                                    {errors.price && <p className="text-red-500 text-sm">{errors.price}</p>}
                                </Field>

                                <Field className="gap-1">
                                    <FieldLabel >Select the Availability Period</FieldLabel>
                                    <Popover>
                                        <PopoverTrigger asChild aria-invalid={(!!errors.from) || (!!errors.to)}>
                                            <Button
                                                variant="outline"
                                                id="date-picker-range"
                                                className="justify-start px-2.5 font-normal"
                                            >
                                                <CalendarIcon />
                                                {date?.from ? (
                                                    date.to ? (
                                                        <>
                                                            {format(date.from, "yyyy-MM-dd")} -{" "}
                                                            {format(date.to, "yyyy-MM-dd")}
                                                        </>
                                                    ) : (
                                                        format(date.from, "yyyy-MM-dd")
                                                    )
                                                ) : (
                                                    <span>Pick a date</span>
                                                )}
                                            </Button>
                                        </PopoverTrigger>
                                        <PopoverContent className="w-auto p-0" align="start">
                                            <Calendar
                                                mode="range"
                                                defaultMonth={date?.from}
                                                selected={date}
                                                onSelect={setDate}
                                                numberOfMonths={2}
                                            />
                                        </PopoverContent>
                                    </Popover>
                                    {errors.startAvailable && <p className="text-red-500 text-sm">{errors.startAvailable}</p>} 
                                    {errors.endAvailable && <p className="text-red-500 text-sm">{errors.endAvailable}</p>}
                                </Field>
                            </div>

                            <Button type="submit">Add Habitation</Button>
                        </FieldGroup>
                    </form>
                </CardContent>
            </Card>
        </div>
    )
}